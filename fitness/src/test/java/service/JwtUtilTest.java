package service;

import kg.biamino.projects.JwtUtil;
import kg.biamino.projects.model.AppUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000;

    private final JwtUtil jwtUtil = new JwtUtil();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
    }

    @Test
    void generateToken_returnsTokenWithExpectedClaims() {
        String token = jwtUtil.generateToken("Akhmat.Tursunbaev");

        assertNotNull(token);
        assertEquals("Akhmat.Tursunbaev", jwtUtil.extractUsername(token));
        assertNotNull(jwtUtil.extractIdToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void extractExpiration_isInTheFuture() {
        String token = jwtUtil.generateToken("Akhmat.Tursunbaev");

        Date expiration = jwtUtil.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_matchingUsername_doesNotThrow() {
        String token = jwtUtil.generateToken("Akhmat.Tursunbaev");
        AppUserDetails userDetails = new AppUserDetails("Akhmat.Tursunbaev", "password", Collections.emptyList());

        jwtUtil.validateToken(token, userDetails);
    }

    @Test
    void validateToken_mismatchedUsername_throwsBadCredentials() {
        String token = jwtUtil.generateToken("Akhmat.Tursunbaev");
        AppUserDetails userDetails = new AppUserDetails("Someone.Else", "password", Collections.emptyList());

        assertThrows(BadCredentialsException.class, () -> jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void extractAllClaims_malformedToken_throwsBadCredentials() {
        assertThrows(BadCredentialsException.class, () -> jwtUtil.extractAllClaims("not-a-real-token"));
    }

    @Test
    void isTokenExpired_expiredToken_throwsBadCredentials() {
        // jjwt validates the `exp` claim while parsing, so an already-expired token
        // never reaches the isTokenExpired() comparison - it fails earlier, inside extractExpiration().
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", -1000L);

        String token = jwtUtil.generateToken("Akhmat.Tursunbaev");

        assertThrows(BadCredentialsException.class, () -> jwtUtil.isTokenExpired(token));
    }
}
