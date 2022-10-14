package at.ac.tuwien.sepm.groupphase.backend.service.mail.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.mail.FrontendPasswordResetLinkBean;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.PasswordResetLinkGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetLinkGeneratorImpl implements PasswordResetLinkGenerator {
  @Autowired FrontendPasswordResetLinkBean resetLink;

  @Override
  public String forResetJwt(String resetJwt) {
    return resetLink.href() + "?jwt=" + resetJwt;
  }
}
