package de.adorsys.datasafe.directory.impl.profile.config;

import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.directory.api.types.CreateUserPrivateProfile;
import de.adorsys.datasafe.directory.api.types.CreateUserPublicProfile;
import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.encrypiton.api.types.keystore.KeyStoreAuth;
import de.adorsys.datasafe.encrypiton.api.types.keystore.ReadStorePassword;
import de.adorsys.datasafe.types.api.resource.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Collections;

/**
 * Default DFS folders layout provider, suitable both for s3 and filesystem.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultDFSConfig implements DFSConfig {

    private static final String USERS_ROOT = "users/";
    private static final String PRIVATE_COMPONENT = "private";
    private static final String PRIVATE_FILES_COMPONENT = PRIVATE_COMPONENT + "/files";
    private static final String PUBLIC_COMPONENT = "public";
    private static final String INBOX_COMPONENT = PUBLIC_COMPONENT + "/" + "inbox";
    private static final String VERSION_COMPONENT = "versions";

    private final Uri systemRoot;
    private final ReadStorePassword systemPassword;
    private final UserProfileLocation userProfileLocation;

    /**
     * @param systemRoot Root location for all files - private files, user profiles, etc. For example you want
     * to place everything in datasafe/system directory within storage
     * @param systemPassword System password to open keystore
     */
    public DefaultDFSConfig(String systemRoot, String systemPassword) {
        this(new Uri(systemRoot), systemPassword);
    }

    /**
     * @param systemRoot Root location for all files - private files, user profiles, etc. For example you want
     * to place everything in datasafe/system directory within storage
     * @param systemPassword System password to open keystore
     */
    public DefaultDFSConfig(URI systemRoot, String systemPassword) {
        this(new Uri(systemRoot), systemPassword);
    }

    /**
     * @param systemRoot Root location for all files - private files, user profiles, etc. For example you want
     * to place everything in datasafe/system directory within storage
     * @param systemPassword System password to open keystore
     */
    public DefaultDFSConfig(Uri systemRoot, String systemPassword) {
        this(systemRoot, systemPassword, new DefaultUserProfileLocationImpl(systemRoot));
    }

    /**
     * @param systemRoot Root location for all files - private files, user profiles, etc. For example you want
     * to place everything in datasafe/system directory within storage
     * @param systemPassword System password to open keystore
     * @param userProfileLocation Bootstrap for user profile files placement
     */
    public DefaultDFSConfig(Uri systemRoot, String systemPassword, UserProfileLocation userProfileLocation) {
        systemRoot = addTrailingSlashIfNeeded(systemRoot);
        this.systemRoot = systemRoot;
        this.systemPassword = new ReadStorePassword(systemPassword);
        this.userProfileLocation = userProfileLocation;
        log.debug("Root is {}", dfsRoot());
    }

    @Override
    public KeyStoreAuth privateKeyStoreAuth(UserIDAuth auth) {
        return new KeyStoreAuth(
                this.systemPassword,
                auth.getReadKeyPassword()
        );
    }

    @Override
    public AbsoluteLocation publicProfile(UserID forUser) {
        return locatePublicProfile(forUser);
    }

    @Override
    public AbsoluteLocation privateProfile(UserID forUser) {
        return locatePrivateProfile(forUser);
    }

    @Override
    public CreateUserPrivateProfile defaultPrivateTemplate(UserIDAuth id) {
        Uri rootLocation = userRoot(id.getUserID());
        Uri keyStoreUri = rootLocation.resolve(PRIVATE_COMPONENT + "/keystore");
        Uri filesUri = rootLocation.resolve(PRIVATE_FILES_COMPONENT + "/");

        return CreateUserPrivateProfile.builder()
                .id(id)
                .privateStorage(accessPrivate(filesUri))
                .keystore(accessPrivate(keyStoreUri))
                .inboxWithWriteAccess(accessPrivate(inbox(rootLocation)))
                .documentVersionStorage(accessPrivate(rootLocation.resolve("./" + VERSION_COMPONENT + "/")))
                .publishPubKeysTo(access(publicKeys(rootLocation)))
                .associatedResources(Collections.singletonList(accessPrivate(rootLocation)))
                .build();
    }

    @Override
    public CreateUserPublicProfile defaultPublicTemplate(UserIDAuth id) {
        Uri rootLocation = userRoot(id.getUserID());

        return CreateUserPublicProfile.builder()
                .id(id.getUserID())
                .inbox(access(inbox(rootLocation)))
                .publicKeys(access(publicKeys(rootLocation)))
                .build();
    }

    /**
     * Where system files like users' private and public profile are located within DFS.
     */
    private AbsoluteLocation<PublicResource> dfsRoot() {
        return new AbsoluteLocation<>(new BasePublicResource(this.systemRoot));
    }

    private AbsoluteLocation<PrivateResource> locatePrivateProfile(UserID ofUser) {
        return userProfileLocation.locatePrivateProfile(ofUser);
    }

    private AbsoluteLocation<PublicResource> locatePublicProfile(UserID ofUser) {
        return userProfileLocation.locatePublicProfile(ofUser);
    }

    private AbsoluteLocation<PublicResource> access(Uri path) {
        return new AbsoluteLocation<>(new BasePublicResource(path));
    }

    private AbsoluteLocation<PrivateResource> accessPrivate(Uri path) {
        return new AbsoluteLocation<>(new BasePrivateResource(path, new Uri(""), new Uri("")));
    }

    private Uri userRoot(UserID userID) {
        return dfsRoot().location().resolve(USERS_ROOT).resolve(userID.getValue() + "/");
    }

    private Uri inbox(Uri rootLocation) {
        return rootLocation.resolve("./" + INBOX_COMPONENT + "/");
    }

    private Uri publicKeys(Uri rootLocation) {
        return rootLocation.resolve("./" + PUBLIC_COMPONENT + "/" + "pubkeys");
    }

    public static Uri addTrailingSlashIfNeeded(Uri systemRoot) {
        return new Uri(addTrailingSlashIfNeeded(systemRoot.asURI()));
    }

    @SneakyThrows
    public static URI addTrailingSlashIfNeeded(URI systemRoot) {
        return new URI(addTrailingSlashIfNeeded(systemRoot.toASCIIString()));
    }

    public static String addTrailingSlashIfNeeded(String systemRoot) {
        if (systemRoot == null) {
            throw new RuntimeException("systemRoot must not be null");
        }
        int last = systemRoot.length();
        if (systemRoot.substring(last-1).equals("/")) {
            return systemRoot;
        }
        return systemRoot + "/";
    }

}
