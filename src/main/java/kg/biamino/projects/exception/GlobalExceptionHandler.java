package kg.biamino.projects.exception;

import jakarta.validation.ConstraintViolationException;
import kg.biamino.projects.dto.ErrorResponseBody;
import kg.biamino.projects.service.ErrorResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler  {
    private final ErrorResponseService errorResponseService;

    public GlobalExceptionHandler(ErrorResponseService errorResponseService) {
        this.errorResponseService = errorResponseService;
    }


    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseBody> illegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<ErrorResponseBody> authenticationException(AuthenticationException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {AuthorizationException.class})
    public ResponseEntity<ErrorResponseBody> authorizationException(AuthorizationException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value=TrainingTypeNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> trainingTypeNotFoundException(TrainingTypeNotFoundException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseBody> constraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={UserNotFoundException.class})
    public ResponseEntity<ErrorResponseBody> userNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value={NoSuchElementException.class})
    public ResponseEntity<ErrorResponseBody> noSuchElementException(NoSuchElementException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.NOT_FOUND);
    }

}
