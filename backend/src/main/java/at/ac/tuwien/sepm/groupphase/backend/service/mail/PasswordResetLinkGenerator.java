package at.ac.tuwien.sepm.groupphase.backend.service.mail;

public interface PasswordResetLinkGenerator {
  /**
   * Generates a password reset link for a given reset JWT.
   *
   * @param resetJwt is used to authenticate against the password reset endpoint.
   * @return a link to the page of the SPA where a user can perform a password reset.
   */
  String forResetJwt(String resetJwt);
}
