package at.ac.tuwien.sepm.groupphase.backend.service.user.password;

public interface PasswordGenerator {
  /**
   * Generates a random alphanumeric password.
   *
   * @return the generated password.
   */
  String generate();
}
