package at.ac.tuwien.sepm.groupphase.backend.exception;

public class AlreadyExistsException extends RuntimeException {

  public AlreadyExistsException(String message) {
    super(message);
  }
}
