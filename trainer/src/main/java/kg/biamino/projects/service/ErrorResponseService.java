package kg.biamino.projects.service;

import kg.biamino.projects.dto.ErrorResponseBody;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorResponseService {
//    ErrorResponseBody makeResponse(IllegalArgumentException message);
//
//    ErrorResponseBody makeResponse(AuthenticationException authenticationException);
//
//    ErrorResponseBody makeResponse(TrainingTypeNotFoundException trainingTypeNotFoundException);
//
//    ErrorResponseBody makeResponse(ConstraintViolationException constraintViolationException);
//
    ErrorResponseBody makeResponse(MethodArgumentNotValidException message);
//
//    ErrorResponseBody makeResponse(AuthorizationException validationException);

    <T extends RuntimeException> ErrorResponseBody makeResponse(T validationException);

    <T extends Exception> ErrorResponseBody makeResponse(T exception);
}
