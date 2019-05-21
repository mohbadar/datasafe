package de.adorsys.datasafe.business.impl.keystore;

import dagger.Binds;
import dagger.Module;
import de.adorsys.datasafe.business.api.encryption.keystore.KeyStoreService;
import de.adorsys.datasafe.business.api.encryption.keystore.PublicKeySerde;
import de.adorsys.datasafe.business.impl.encryption.keystore.KeyStoreServiceImpl;
import de.adorsys.datasafe.business.impl.encryption.keystore.PublicKeySerdeImpl;

/**
 * This module provides keystore management operations.
 */
@Module
public abstract class DefaultKeyStoreModule {

    @Binds
    public abstract PublicKeySerde publicKeySerde(PublicKeySerdeImpl impl);

    @Binds
    public abstract KeyStoreService keyStoreService(KeyStoreServiceImpl impl);
}
