package at.ac.tuwien.sepm.groupphase.backend.service.mail;

public class PasswordResetMail extends Mail {
  private final String recipient;
  private final String resetJwt;

  private final PasswordResetLinkGenerator generator;

  public PasswordResetMail(
      PasswordResetLinkGenerator generator, String recipient, String resetJwt) {
    this.generator = generator;
    this.recipient = recipient;
    this.resetJwt = resetJwt;
  }

  @Override
  public String getRecipient() {
    return this.recipient;
  }

  @Override
  public String getSubject() {
    return "Password Reset For Your TicketLine Account";
  }

  @Override
  public String getBody() {
    return String.format(
        """
      <html>
        <head>
          <title>Password Reset</title>
        </head>
        <body>
          <p>Your password for TicketLine has been requested to be reset.</p>
          <p>Use <a href="%s">this link</a> to reset your password.</p>
        </body>
      </html>
      """
            .strip()
            .replace("\n", "")
            .trim(),
        generator.forResetJwt(resetJwt));
  }
}
