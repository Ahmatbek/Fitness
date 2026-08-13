package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.ErrorResponseBody;
import kg.biamino.projects.service.ErrorResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;


@Slf4j
@Service
public class ErrorResponseServiceImpl implements ErrorResponseService {


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



    @Override
    public <T extends RuntimeException> ErrorResponseBody makeResponse(T validationException) {
        String mess = Optional.ofNullable(validationException.getMessage())
                .orElse("authorization exception occurred");

        log.warn(mess);
        return ErrorResponseBody.builder()
                .title(validationException.getClass().getSimpleName())
                .details(Map.of("errors", List.of(mess)))
                .build();

    }

    @Override
    public <T extends Exception> ErrorResponseBody makeResponse(T exception) {
        String mess = Optional.ofNullable(exception.getMessage())
                .orElse("authorization exception occurred");

        return ErrorResponseBody.builder()
                .title(exception.getClass().getSimpleName())
                .details(Map.of("errors", List.of(mess)))
                .build();


    }
}
