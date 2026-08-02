package config;

import kg.biamino.projects.config.JwtLogoutTokens;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtLogoutTokensTest {

    private final JwtLogoutTokens jwtLogoutTokens = new JwtLogoutTokens();

    @Test
    void isInvalid_unknownJti_returnsFalse() {
        assertFalse(jwtLogoutTokens.isInvalid("never-seen"));
    }

    @Test
    void invalidate_thenIsInvalid_returnsTrue() {
        jwtLogoutTokens.invalidate("jti-1", Instant.now().plus(1, ChronoUnit.HOURS));

        assertTrue(jwtLogoutTokens.isInvalid("jti-1"));
    }

    @Test
    void cleanUp_removesExpiredEntriesButKeepsFutureOnes() {
        jwtLogoutTokens.invalidate("expired", Instant.now().minus(1, ChronoUnit.HOURS));
        jwtLogoutTokens.invalidate("still-valid", Instant.now().plus(1, ChronoUnit.HOURS));

        jwtLogoutTokens.cleanUp();

        assertFalse(jwtLogoutTokens.isInvalid("expired"));
        assertTrue(jwtLogoutTokens.isInvalid("still-valid"));
    }
}
