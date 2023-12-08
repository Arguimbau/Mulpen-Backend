package dk.kea.mulpenbackend.exception;

public class UnexpectedFileTypeException extends RuntimeException {
  public UnexpectedFileTypeException(String message) {
    super(message);
  }
}
