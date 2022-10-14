package at.ac.tuwien.sepm.groupphase.backend.service.mail;

public class FrontendPasswordResetLinkBean {
  private final String href;

  public FrontendPasswordResetLinkBean(String href) {
    this.href = href;
  }

  public String href() {
    return this.href;
  }
}
