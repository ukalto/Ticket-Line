package at.ac.tuwien.sepm.groupphase.backend.service.mail.impl;

import at.ac.tuwien.sepm.groupphase.backend.exception.MailDeliveryFailedException;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.Mail;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.MailService;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class GmailMailService implements MailService {
  @Autowired private JavaMailSender mailSender;

  @Override
  public void send(Mail mail) {
    var mimeMessage = this.mailSender.createMimeMessage();
    var mailToSend = new MimeMessageHelper(mimeMessage, "UTF-8");

    try {
      mimeMessage.setFrom("SEPMTicketLineProject@gmail.com");
      mimeMessage.setSubject(mail.getSubject());
      mailToSend.setTo(mail.getRecipient());
      mimeMessage.setContent(mail.getBody(), "text/html; charset=utf-8");

      this.mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      throw new MailDeliveryFailedException(e.getMessage());
    }
  }
}
