package at.ac.tuwien.sepm.groupphase.backend.exception;

public class MailDeliveryFailedException extends RuntimeException {

  public MailDeliveryFailedException(String message) {
    super(message);
  }
}
