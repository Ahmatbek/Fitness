package kg.biamino.projects.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter implements AuthenticationConverter {
    private final static String TOKEN_PREFIX = "Bearer ";
    @Override
    public Authentication convert(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return new UsernamePasswordAuthenticationToken(token.substring(TOKEN_PREFIX.length()), null);
        }
        return null;
    }
}

