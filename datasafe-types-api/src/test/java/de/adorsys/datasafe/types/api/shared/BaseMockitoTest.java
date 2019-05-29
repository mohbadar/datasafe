package de.adorsys.datasafe.types.api.shared;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * This class configures Mockito so that it will inject annotated mocks and display exceptions always
 * at correct places.
 * Tests that use Mockito should use this class.
 */
public abstract class BaseMockitoTest {

    @BeforeEach
    public void setup() {
        System.setProperty("SECURE_LOGS", "off");
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void validate() {
        Mockito.validateMockitoUsage();
    }
}
