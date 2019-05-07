package de.adorsys.datasafe.business.api.encryption.document;

import de.adorsys.datasafe.business.api.types.keystore.PublicKeyIDWithPublicKey;
import de.adorsys.datasafe.business.api.types.keystore.SecretKeyIDWithKey;
import de.adorsys.datasafe.business.api.types.resource.AbsoluteResourceLocation;
import de.adorsys.datasafe.business.api.types.resource.PrivateResource;
import de.adorsys.datasafe.business.api.types.resource.PublicResource;

import java.io.OutputStream;

/**
 * File write operation at a given location.
 */
public interface EncryptedDocumentWriteService {

    OutputStream write(AbsoluteResourceLocation<PublicResource> location, PublicKeyIDWithPublicKey publicKey);
    OutputStream write(AbsoluteResourceLocation<PrivateResource> location, SecretKeyIDWithKey secretKey);
}


