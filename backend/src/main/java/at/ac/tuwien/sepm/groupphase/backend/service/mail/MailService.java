package at.ac.tuwien.sepm.groupphase.backend.service.mail;

public interface MailService {
  /**
   * Sends an email to a given recipient.
   *
   * @param mail to send.
   */
  void send(Mail mail);
}
