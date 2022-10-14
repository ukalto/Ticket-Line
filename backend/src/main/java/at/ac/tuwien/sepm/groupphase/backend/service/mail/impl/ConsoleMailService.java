package at.ac.tuwien.sepm.groupphase.backend.service.mail.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.mail.Mail;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.MailService;

// @Service
public class ConsoleMailService implements MailService {
  public ConsoleMailService() {}

  @Override
  public void send(Mail mail) {
    final var messageToSend =
        String.format(
            "Recipient: %s\nHeader: %s\nBody: %s",
            mail.getRecipient(), mail.getSubject(), mail.getBody());
    System.out.println(messageToSend);
  }
}
