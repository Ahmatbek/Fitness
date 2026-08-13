package exception;

import kg.biamino.projects.dto.ErrorResponseBody;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.exception.TrainerNotFoundException;
import kg.biamino.projects.service.ErrorResponseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ErrorResponseService errorResponseService;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleException_trainerNotFound_returns404WithBody() {
        TrainerNotFoundException exception = new TrainerNotFoundException("Bekzat.Isakov");
        ErrorResponseBody body = ErrorResponseBody.builder().title("TrainerNotFoundException").build();
        when(errorResponseService.makeResponse(exception)).thenReturn(body);

        ResponseEntity<ErrorResponseBody> response = globalExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(body, response.getBody());
    }

    @Test
    void methodArgumentNotValidException_returns400WithBody() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        ErrorResponseBody body = ErrorResponseBody.builder().title("validation error").build();
        when(errorResponseService.makeResponse(exception)).thenReturn(body);

        ResponseEntity<ErrorResponseBody> response = globalExceptionHandler.methodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(body, response.getBody());
    }
}
