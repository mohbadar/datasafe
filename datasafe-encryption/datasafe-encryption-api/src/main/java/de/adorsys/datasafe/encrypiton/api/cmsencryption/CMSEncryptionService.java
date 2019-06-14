package de.adorsys.datasafe.encrypiton.api.cmsencryption;

import de.adorsys.datasafe.encrypiton.api.types.keystore.KeyID;
import de.adorsys.datasafe.encrypiton.api.types.keystore.PublicKeyIDWithPublicKey;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Set;
import java.util.function.Function;


/**
 * Interface for performing content-stream encryption and decryption.
 */
public interface CMSEncryptionService {

    /**
     * Builds asymmetrically encrypted stream using public-key cryptography.
     * @param dataContentStream Stream to encrypt
     * @param publicKeyIDWithPublicKey Contains user public key and
     *        public-key ID which gets embedded into a stream, used for finding the private key to decrypt
     * @return Encrypted stream that wraps {@code dataContentStream}
     * @apiNote Closes underlying stream when result is closed
     */
    OutputStream buildEncryptionOutputStream(OutputStream dataContentStream, PublicKeyIDWithPublicKey publicKeyIDWithPublicKey);

    /**
     * Builds asymmetrically encrypted stream using public-key cryptography.
     * @param dataContentStream Stream to encrypt
     * @param publicKeyIDWithPublicKeySet Contains user public key and
     *        public-key ID which gets embedded into a stream, used for finding the private key to decrypt
     * @return Encrypted stream that wraps {@code dataContentStream}
     * @apiNote Closes underlying stream when result is closed
     */
    OutputStream buildEncryptionOutputStream(OutputStream dataContentStream, Set<PublicKeyIDWithPublicKey> publicKeyIDWithPublicKeySet);

    /**
     * Builds symmetrically encrypted stream.
     * @param dataContentStream Stream to encrypt
     * @param secretKey User secret key
     * @param secretKeyID User key ID gets embedded into a stream, used for finding the key to decrypt
     * @return Encrypted stream that wraps {@code dataContentStream}
     * @apiNote Closes underlying stream when result is closed
     */
    OutputStream buildEncryptionOutputStream(OutputStream dataContentStream, SecretKey secretKey, KeyID secretKeyID);

    /**
     * Builds decrypted stream out of encrypted one.
     * @param inputStream Stream to decrypt
     * @param keyById Key to its ID mapping, will retrieve key for decryption using this
     * @return Decrypted stream that wraps {@code inputStream}
     * @apiNote Closes underlying stream when result is closed
     */
    InputStream buildDecryptionInputStream(InputStream inputStream, Function<String, Key> keyById);
}
