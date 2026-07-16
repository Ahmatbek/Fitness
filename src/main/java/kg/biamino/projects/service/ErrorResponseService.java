package kg.biamino.projects.service;

import jakarta.validation.ConstraintViolationException;
import kg.biamino.projects.dto.ErrorResponseBody;
import kg.biamino.projects.exception.AuthenticationException;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.TrainingTypeNotFoundException;
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
}
