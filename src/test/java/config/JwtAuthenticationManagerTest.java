package config;

import kg.biamino.projects.config.JwtAuthenticationManager;
import kg.biamino.projects.config.JwtLogoutTokens;
import kg.biamino.projects.config.JwtUtil;
import kg.biamino.projects.model.AppUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationManagerTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtLogoutTokens jwtLogoutTokens;

    private JwtAuthenticationManager jwtAuthenticationManager;

    @BeforeEach
    void setUp() {
        jwtAuthenticationManager = new JwtAuthenticationManager(jwtUtil, userDetailsService, jwtLogoutTokens);
    }

    private Authentication tokenAuthentication(String rawToken) {
        return new UsernamePasswordAuthenticationToken(rawToken, null);
    }

    @Test
    void authenticate_validToken_returnsAuthenticationWithUserAuthorities() {
        AppUserDetails userDetails = new AppUserDetails("Aidana.Toktosunova", "password",
                List.of(new SimpleGrantedAuthority("TRAINEE")));
        when(jwtUtil.extractIdToken("valid-token")).thenReturn("jti-1");
        when(jwtLogoutTokens.isInvalid("jti-1")).thenReturn(false);
        when(jwtUtil.extractUsername("valid-token")).thenReturn("Aidana.Toktosunova");
        when(userDetailsService.loadUserByUsername("Aidana.Toktosunova")).thenReturn(userDetails);

        Authentication result = jwtAuthenticationManager.authenticate(tokenAuthentication("valid-token"));

        assertEquals(userDetails, result.getPrincipal());
        assertEquals(1, result.getAuthorities().size());
        verify(jwtUtil).validateToken("valid-token", userDetails);
    }

    @Test
    void authenticate_loggedOutToken_throwsBadCredentials() {
        when(jwtUtil.extractIdToken("logged-out-token")).thenReturn("jti-2");
        when(jwtLogoutTokens.isInvalid("jti-2")).thenReturn(true);

        assertThrows(BadCredentialsException.class,
                () -> jwtAuthenticationManager.authenticate(tokenAuthentication("logged-out-token")));

        verifyNoInteractions(userDetailsService);
    }

    @Test
    void authenticate_noUsernameInToken_throwsBadCredentials() {
        when(jwtUtil.extractIdToken("bad-token")).thenReturn("jti-3");
        when(jwtLogoutTokens.isInvalid("jti-3")).thenReturn(false);
        when(jwtUtil.extractUsername("bad-token")).thenReturn(null);

        assertThrows(BadCredentialsException.class,
                () -> jwtAuthenticationManager.authenticate(tokenAuthentication("bad-token")));

        verifyNoInteractions(userDetailsService);
    }

    @Test
    void authenticate_disabledUser_throwsDisabledException() {
        UserDetails disabledUser = mock(UserDetails.class);
        when(disabledUser.isEnabled()).thenReturn(false);
        when(jwtUtil.extractIdToken("disabled-token")).thenReturn("jti-4");
        when(jwtLogoutTokens.isInvalid("jti-4")).thenReturn(false);
        when(jwtUtil.extractUsername("disabled-token")).thenReturn("Someone");
        when(userDetailsService.loadUserByUsername("Someone")).thenReturn(disabledUser);

        assertThrows(DisabledException.class,
                () -> jwtAuthenticationManager.authenticate(tokenAuthentication("disabled-token")));

        verify(jwtUtil, never()).validateToken(any(), any());
    }

    @Test
    void authenticate_expiredOrMismatchedToken_propagatesBadCredentials() {
        AppUserDetails userDetails = new AppUserDetails("Aidana.Toktosunova", "password", List.of());
        when(jwtUtil.extractIdToken("expired-token")).thenReturn("jti-5");
        when(jwtLogoutTokens.isInvalid("jti-5")).thenReturn(false);
        when(jwtUtil.extractUsername("expired-token")).thenReturn("Aidana.Toktosunova");
        when(userDetailsService.loadUserByUsername("Aidana.Toktosunova")).thenReturn(userDetails);
        doThrow(new BadCredentialsException("Invalid token")).when(jwtUtil).validateToken("expired-token", userDetails);

        assertThrows(BadCredentialsException.class,
                () -> jwtAuthenticationManager.authenticate(tokenAuthentication("expired-token")));
    }
}
