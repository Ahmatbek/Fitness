package config;

import jakarta.servlet.http.HttpServletRequest;
import kg.biamino.projects.config.JwtAuthenticationConverter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationConverterTest {

    private final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    @Test
    void convert_bearerHeader_returnsAuthenticationWithRawToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

        Authentication authentication = converter.convert(request);

        assertEquals("abc.def.ghi", authentication.getPrincipal());
    }

    @Test
    void convert_missingHeader_returnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        assertNull(converter.convert(request));
    }

    @Test
    void convert_nonBearerHeader_returnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        assertNull(converter.convert(request));
    }
}
