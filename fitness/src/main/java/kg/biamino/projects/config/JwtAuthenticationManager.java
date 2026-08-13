package kg.biamino.projects.config;

import kg.biamino.projects.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


@Component
public class JwtAuthenticationManager implements AuthenticationManager {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JwtLogoutTokens jwtLogoutTokens;

    public JwtAuthenticationManager(JwtUtil jwtUtil, UserDetailsService userDetailsService, JwtLogoutTokens jwtLogoutTokens) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtLogoutTokens = jwtLogoutTokens;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String authToken = authentication.getPrincipal().toString();
        String jtiId = jwtUtil.extractIdToken(authToken);
        if(jwtLogoutTokens.isInvalid(jtiId)) {
            throw new BadCredentialsException("You have been logged out");
        }

        String username = jwtUtil.extractUsername(authToken);
        if (username == null) {
            throw new BadCredentialsException("Invalid token");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!userDetails.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
        jwtUtil.validateToken(authToken, userDetails);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
