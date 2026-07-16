package kg.biamino.projects.service.impl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kg.biamino.projects.dto.ErrorResponseBody;
import kg.biamino.projects.exception.AuthenticationException;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.TrainingTypeNotFoundException;
import kg.biamino.projects.service.ErrorResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

@Slf4j
@Service
public class ErrorResponseServiceImpl implements ErrorResponseService {

//    @Override
//    public ErrorResponseBody makeResponse(IllegalArgumentException message) {
//
//        String mess = Optional.ofNullable(message.getMessage())
//                .orElse("Exception occurred");
//        log.error(mess);
//        return ErrorResponseBody.builder()
//                .title("Error")
//                .details(Map.of("errors", List.of(mess)))
//                .build();
//    }
//
//    @Override
//    public ErrorResponseBody makeResponse(AuthenticationException authenticationException) {
//        String mess = Optional.ofNullable(authenticationException.getMessage()).orElse("Exception occurred");
//        return ErrorResponseBody.builder()
//                .title("Authentication Failed")
//                .details(Map.of("errors", List.of(authenticationException.getMessage())))
//                .build();
//    }
//
//    @Override
//    public ErrorResponseBody makeResponse(TrainingTypeNotFoundException trainingTypeNotFoundException) {
//        String mes = Optional.ofNullable(trainingTypeNotFoundException.getMessage()).orElse("Training Type  error");
//
//        return ErrorResponseBody.builder()
//                .title("Training Type Error")
//                .details(Map.of("errors", List.of(mes)))
//                .build();
//
//    }
//
//    @Override
//    public ErrorResponseBody makeResponse(ConstraintViolationException constraintViolationException) {
//        Set<ConstraintViolation<?>> mess = constraintViolationException.getConstraintViolations();
//
//        List<String> errors = mess.stream()
//                .map(ConstraintViolation::getMessage)
//                .toList();
//
//        return ErrorResponseBody.builder()
//                .title("Constraint Violation")
//                .details(Map.of("error", errors))
//                .build();
//    }

    @Override
    public ErrorResponseBody makeResponse(MethodArgumentNotValidException message) {
        ErrorResponseBody errorResponseBody = new ErrorResponseBody();
        Map<String, List<String>> errors = new HashMap<>();

        message.getBindingResult().getFieldErrors().stream()
                .filter(e-> e.getDefaultMessage()!=null)
                .forEach(fieldError -> {
                    if(!errors.containsKey(fieldError.getField())) {
                        errors.computeIfAbsent(fieldError.getField(), k-> new ArrayList<>()).add(fieldError.getDefaultMessage());
                    }else{
                        Objects.requireNonNull(errors.compute(fieldError.getField(), (k, v) -> v)).add(fieldError.getDefaultMessage());
                    }


                });

        errorResponseBody.setTitle(message.getBindingResult().getObjectName());
        errorResponseBody.setDetails(errors);

        return errorResponseBody;
    }
//
//    @Override
//    public ErrorResponseBody makeResponse(AuthorizationException validationException) {
//        String mess = Optional.ofNullable(validationException.getMessage())
//                .orElse("authorization exception occurred");
//
//        return ErrorResponseBody.builder()
//                .title("Authorization Error")
//                .details(Map.of("errors", List.of(mess)))
//                .build();
//
//    }


    @Override
    public <T extends RuntimeException> ErrorResponseBody makeResponse(T validationException) {
        String mess = Optional.ofNullable(validationException.getMessage())
                .orElse("authorization exception occurred");

        return ErrorResponseBody.builder()
                .title(validationException.getClass().getSimpleName())
                .details(Map.of("errors", List.of(mess)))
                .build();

    }
}
