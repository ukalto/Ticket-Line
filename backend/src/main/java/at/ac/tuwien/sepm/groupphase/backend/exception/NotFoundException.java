package at.ac.tuwien.sepm.groupphase.backend.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
