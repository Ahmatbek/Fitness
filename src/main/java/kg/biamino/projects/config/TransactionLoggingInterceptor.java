package kg.biamino.projects.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Component
public class TransactionLoggingInterceptor implements HandlerInterceptor {
    private Logger log =  LoggerFactory.getLogger(TransactionLoggingInterceptor.class.getName());
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String transactionId = request.getHeader(TRANSACTION_ID);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID, transactionId);
        log.info(TRANSACTION_ID + ": " + transactionId, request.getMethod());

        response.setHeader(TRANSACTION_ID, transactionId);
        return true;
    }


}
