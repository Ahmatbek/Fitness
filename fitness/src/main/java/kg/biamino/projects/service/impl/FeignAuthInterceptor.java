package kg.biamino.projects.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs != null) {
            String authHeader = attrs.getRequest().getHeader("Authorization");
            if (authHeader != null) {
                requestTemplate.header("Authorization", authHeader);
            }
        }

        String transactionId = MDC.get(TRANSACTION_ID);
        if (transactionId != null) {
            requestTemplate.header(TRANSACTION_ID, transactionId);
        }
    }
}