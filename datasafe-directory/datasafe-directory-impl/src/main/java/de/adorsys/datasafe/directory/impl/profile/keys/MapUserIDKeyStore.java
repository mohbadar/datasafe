package de.adorsys.datasafe.directory.impl.profile.keys;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class MapUserIDKeyStore {
    @Delegate(types=LombokGemericMap.class, excludes = LombokComputeIfAbsent.class)
    private final Map<UserID, KeyStore> map;


    /**
     * This method must not be called. It is task of the DefaultKeyStoreCache
     * to generate a new Uber Key Store. For that the password is needed which is
     * not provided in this method here.
     */
    /*
    public KeyStore computeIfAbsent(UserID key, Function<? super UserID, ? extends KeyStore> mappingFunction) {

        RuntimeException e = new RuntimeException("Must not be called");
        log.error(e.getMessage(), e);
        throw e;
    }
     */

    public KeyStore computeIfAbsent(UserIDAuth userIDAuth, Function<? super UserID, ? extends KeyStore> mappingFunction) {
        if (map.containsKey(userIDAuth.getUserID())) {
            return map.get(userIDAuth.getUserID());
        }
        KeyStore keyStore = mappingFunction.apply(userIDAuth.getUserID());
        if (! "UBER".equals(keyStore.getType())) {
            keyStore = convertKeyStoreToUberKeyStore(userIDAuth, keyStore);
        }
        map.put(userIDAuth.getUserID(), keyStore);
        return keyStore;

    }

    @SneakyThrows
    private KeyStore convertKeyStoreToUberKeyStore(UserIDAuth currentCredentials, KeyStore current) {
        log.warn("the keystore is of type {} and will be converted to uber in cache", current.getType() );

        KeyStore newKeystore = KeyStore.getInstance("UBER");
        newKeystore.load(null, null);
        Enumeration<String> aliases = current.aliases();

        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Key currentKey = current.getKey(alias, currentCredentials.getReadKeyPassword().getValue());
            newKeystore.setKeyEntry(
                    alias,
                    currentKey,
                    currentCredentials.getReadKeyPassword().getValue(),
                    current.getCertificateChain(alias)
            );
        }

        return newKeystore;
    }



    private interface ComputeIfAbsent<K,V> {
        V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);
    }

    private abstract class LombokGemericMap implements Map<UserID, KeyStore> {
    }

    private abstract class LombokComputeIfAbsent implements ComputeIfAbsent<UserID, KeyStore> {
    }


}
