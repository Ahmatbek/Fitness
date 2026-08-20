package kg.biamino.projects.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Component
public class TransactionLoggingInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(TransactionLoggingInterceptor.class.getName());
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String transactionId = request.getHeader(TRANSACTION_ID);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID, transactionId);
        log.info("Endpoint called: {} {}", request.getMethod(), request.getRequestURI());

        response.setHeader(TRANSACTION_ID, transactionId);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) throws Exception {
        if (ex != null) log.warn("Response: {} {} -> {} ({})", request.getMethod(), request.getRequestURI(), response.getStatus(), ex.getMessage());
        else log.info("Response: {} {} -> {}", request.getMethod(), request.getRequestURI(), response.getStatus());

        MDC.remove(TRANSACTION_ID);
    }

}
