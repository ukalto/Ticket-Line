package at.ac.tuwien.sepm.groupphase.backend.config;

import at.ac.tuwien.sepm.groupphase.backend.service.mail.FrontendPasswordResetLinkBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FrontendConfig {
  @Autowired private Environment environment;

  @Bean
  public FrontendPasswordResetLinkBean getFrontendPasswordResetLinkBean() {
    var href = environment.getProperty("spring.frontend.password-reset-url");

    return new FrontendPasswordResetLinkBean(href);
  }
}
