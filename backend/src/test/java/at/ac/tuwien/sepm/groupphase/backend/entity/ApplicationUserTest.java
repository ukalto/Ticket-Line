package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApplicationUserTest {
  @Test
  void exceededAuthenticationAttempts_shouldBeLocked() {
    var user =
        new ApplicationUser(
            "",
            "",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);
    user.setFailedAuthenticationAttempts(ApplicationUser.MAX_AUTHENTICATION_ATTEMPTS);

    Assertions.assertFalse(user.isAccountNonLocked());

    user.setFailedAuthenticationAttempts(ApplicationUser.MAX_AUTHENTICATION_ATTEMPTS + 1);
    Assertions.assertFalse(user.isAccountNonLocked());
  }
}
