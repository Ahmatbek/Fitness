package config;

import kg.biamino.projects.config.BruteForceProtectionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BruteForceProtectionServiceTest {


    private final BruteForceProtectionService bruteForceProtectionService = new BruteForceProtectionService();



    @Test
    void isBlocked_noAttempts_returnsFalse() {
        assertFalse(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));
    }

    @Test
    void isBlocked_underThreeFailures_returnsFalse() {
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");

        assertFalse(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));
    }

    @Test
    void isBlocked_threeFailures_returnsTrue() {
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");

        assertTrue(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));
    }

    @Test
    void loginSucceeded_resetsAttemptsAndLock() {
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");

        bruteForceProtectionService.loginSucceeded("Aidana.Toktosunova");

        assertFalse(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));

        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        assertFalse(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));
    }

    @Test
    void isBlocked_lockExpired_returnsFalseAndClearsLock() {
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        assertTrue(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));

        @SuppressWarnings("unchecked")
        Map<String, Long> lockCache = (Map<String, Long>) ReflectionTestUtils.getField(bruteForceProtectionService, "lockCache");
        lockCache.put("Aidana.Toktosunova", System.currentTimeMillis() - 6 * 60 * 1000);

        assertFalse(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));
    }

    @Test
    void differentUsers_areTrackedIndependently() {
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");
        bruteForceProtectionService.loginFailed("Aidana.Toktosunova");

        assertTrue(bruteForceProtectionService.isBlocked("Aidana.Toktosunova"));
        assertFalse(bruteForceProtectionService.isBlocked("Dilmurod.Sadyrov"));
    }
}
