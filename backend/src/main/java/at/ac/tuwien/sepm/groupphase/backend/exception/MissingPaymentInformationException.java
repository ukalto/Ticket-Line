package at.ac.tuwien.sepm.groupphase.backend.exception;

public class MissingPaymentInformationException extends RuntimeException {
  public MissingPaymentInformationException(String message) {
    super(message);
  }
}
