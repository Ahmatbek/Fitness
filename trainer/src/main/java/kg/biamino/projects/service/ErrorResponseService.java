package kg.biamino.projects.service;

import kg.biamino.projects.dto.ErrorResponseBody;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorResponseService {
    ErrorResponseBody makeResponse(MethodArgumentNotValidException message);

    <T extends RuntimeException> ErrorResponseBody makeResponse(T validationException);

    <T extends Exception> ErrorResponseBody makeResponse(T exception);
}
