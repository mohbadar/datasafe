package de.adorsys.datasafe.encrypiton.api.types.keystore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper that contains count of public-key pairs and count of encryption keys.
 */
@Getter
@RequiredArgsConstructor
public class KeyCreationConfig {

    public static final String PATH_KEY_ID_PREFIX = "PATH_SECRET";
    public static final String PATH_KEY_ID_PREFIX_CTR = "PATH_CTR_SECRET_";
    public static final String DOCUMENT_KEY_ID_PREFIX = "PRIVATE_SECRET";

    private final int encKeyNumber;
    private final int signKeyNumber;
}