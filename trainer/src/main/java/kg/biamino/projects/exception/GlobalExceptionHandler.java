package kg.biamino.projects.exception;

import kg.biamino.projects.dto.ErrorResponseBody;
import kg.biamino.projects.service.ErrorResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorResponseService errorResponseService;
    public GlobalExceptionHandler(ErrorResponseService errorResponseService) {
        this.errorResponseService = errorResponseService;
    }

    @ExceptionHandler(value = TrainerNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleException(TrainerNotFoundException exception) {
        return new ResponseEntity<>(errorResponseService.makeResponse(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(errorResponseService.makeResponse(e), HttpStatus.BAD_REQUEST);
    }
}
