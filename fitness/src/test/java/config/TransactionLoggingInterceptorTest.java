package config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.biamino.projects.config.TransactionLoggingInterceptor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionLoggingInterceptorTest {

    private final TransactionLoggingInterceptor interceptor = new TransactionLoggingInterceptor();

    @Test
    void preHandle_noTransactionIdHeader_generatesNewOne() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader("transactionId")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("transactionId"), captor.capture());
        assertNotNull(captor.getValue());
        assertEquals(captor.getValue(), MDC.get("transactionId"));
    }

    @Test
    void preHandle_blankTransactionIdHeader_generatesNewOne() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader("transactionId")).thenReturn("   ");
        when(request.getMethod()).thenReturn("GET");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(response).setHeader(eq("transactionId"), org.mockito.ArgumentMatchers.argThat(id -> id != null && !id.isBlank()));
    }

    @Test
    void preHandle_existingTransactionIdHeader_reusesIt() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader("transactionId")).thenReturn("existing-tx-id");
        when(request.getMethod()).thenReturn("POST");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(response).setHeader("transactionId", "existing-tx-id");
        assertEquals("existing-tx-id", MDC.get("transactionId"));
    }

    @Test
    void afterCompletion_success_clearsMdc() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/trainings");
        when(response.getStatus()).thenReturn(200);
        MDC.put("transactionId", "some-id");

        interceptor.afterCompletion(request, response, new Object(), null);

        assertNull(MDC.get("transactionId"));
    }

    @Test
    void afterCompletion_withException_clearsMdc() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/trainings");
        when(response.getStatus()).thenReturn(500);
        MDC.put("transactionId", "some-id");

        interceptor.afterCompletion(request, response, new Object(), new RuntimeException("boom"));

        assertNull(MDC.get("transactionId"));
    }
}
