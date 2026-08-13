package exception;

import jakarta.validation.ConstraintViolationException;
import kg.biamino.projects.exception.AuthenticationException;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.DateInvalidException;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.exception.TrainingTypeNotFoundException;
import kg.biamino.projects.exception.UserInactiveException;
import kg.biamino.projects.exception.UserNotFoundException;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(new ErrorResponseServiceImpl());

    @Test
    void illegalArgumentException_returns404() {
        ResponseEntity<?> response = handler.illegalArgumentException(new IllegalArgumentException("bad arg"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void authenticationException_returns401() {
        ResponseEntity<?> response = handler.authenticationException(new AuthenticationException("bad credentials"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void authorizationException_returns403() {
        ResponseEntity<?> response = handler.authorizationException(new AuthorizationException("not allowed"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void trainingTypeNotFoundException_returns404() {
        ResponseEntity<?> response = handler.trainingTypeNotFoundException(new TrainingTypeNotFoundException("not found"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void constraintViolationException_returns400() {
        ConstraintViolationException exception = new ConstraintViolationException(Set.of());
        ResponseEntity<?> response = handler.constraintViolationException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void methodArgumentNotValidException_returns400() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());
        when(bindingResult.getObjectName()).thenReturn("dto");

        ResponseEntity<?> response = handler.methodArgumentNotValidException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void userNotFoundException_returns404() {
        ResponseEntity<?> response = handler.userNotFoundException(new UserNotFoundException("no user"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void noSuchElementException_returns404() {
        ResponseEntity<?> response = handler.noSuchElementException(new NoSuchElementException("missing"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void dateInvalidException_returns400() {
        ResponseEntity<?> response = handler.dateInvalidException(new DateInvalidException("bad date"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void badCredentialsException_returns401() {
        ResponseEntity<?> response = handler.badCredentialsException(new BadCredentialsException("bad creds"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void userInactiveException_returns401() {
        ResponseEntity<?> response = handler.userInactiveException(new UserInactiveException("inactive"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
