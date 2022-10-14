package at.ac.tuwien.sepm.groupphase.backend.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
  @Autowired private Environment environment;

  @Bean
  public JavaMailSender getMailSender() {
    var mailSender = new JavaMailSenderImpl();

    mailSender.setHost(environment.getProperty("spring.mail.host"));
    mailSender.setPort(Integer.valueOf(environment.getProperty("spring.mail.port")));
    mailSender.setUsername(environment.getProperty("spring.mail.username"));
    mailSender.setPassword(environment.getProperty("spring.mail.password"));

    var mailProperties = new Properties();

    mailProperties.put("mail.smtp.starttls.enable", "true");
    mailProperties.put("mail.smtp.starttls.required", "true");
    mailProperties.put("mail.smtp.auth", "true");
    mailProperties.put("mail.transport.protocol", "smtp");
    mailProperties.put("mail.protocol", "smtp");
    mailProperties.put("mail.debug", "true");
    mailProperties.put("mail.smtp.allow8bitmime", true);
    mailProperties.put("mail.smtps.allow8bitmime", true);

    mailSender.setJavaMailProperties(mailProperties);

    return mailSender;
  }
}
