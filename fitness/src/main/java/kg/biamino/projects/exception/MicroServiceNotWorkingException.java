package kg.biamino.projects.exception;

public class MicroServiceNotWorkingException extends RuntimeException {
    public MicroServiceNotWorkingException(String message) {
        super(message);
    }
}
