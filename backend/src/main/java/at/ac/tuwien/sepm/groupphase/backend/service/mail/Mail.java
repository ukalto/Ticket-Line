package at.ac.tuwien.sepm.groupphase.backend.service.mail;

public abstract class Mail {
  public abstract String getRecipient();

  public abstract String getSubject();

  public abstract String getBody();
}
