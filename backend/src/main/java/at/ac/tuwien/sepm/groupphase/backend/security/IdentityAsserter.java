package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class IdentityAsserter {
  private static final String USER_ID_CLAIM = "uid";

  private final SecurityProperties securityProperties;
  private final UserService userService;

  public IdentityAsserter(SecurityProperties securityProperties, UserService userService) {
    this.securityProperties = securityProperties;
    this.userService = userService;
  }

  private Optional<String> tryExtractEmail(Claims claims) {
    final var email = claims.getSubject();

    return Optional.ofNullable(email);
  }

  private Optional<Integer> tryExtractId(Claims claims) {
    final var uid = (Integer) claims.get(USER_ID_CLAIM);

    return Optional.ofNullable(uid);
  }

  private Claims extractClaims(String bearer) {
    byte[] signingKey = securityProperties.getJwtSecret().getBytes();

    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(bearer.replace(securityProperties.getAuthTokenPrefix(), ""))
        .getBody();
  }

  public void assertMatch(Long requestedUserId, String authorizationHeader) {
    // Assumption: JwtAuthorizationFilter failed already
    // on invalid headers.
    final var parts = authorizationHeader.split(" ");
    final var bearer = parts[1];

    final var claims = this.extractClaims(bearer);

    final var maybeUserId = this.tryExtractId(claims);
    final var containsUserId = maybeUserId.isPresent();
    if (containsUserId) {
      final var userId = maybeUserId.get();
      final var isMismatch = requestedUserId.intValue() != userId;
      if (isMismatch) {
        throw new IllegalStateException("Requested identity does not match provided");
      }

      return;
    }

    final var maybeEmail = this.tryExtractEmail(claims);
    final var containsEmail = maybeEmail.isPresent();
    if (containsEmail) {
      final var user = this.userService.findApplicationUserByEmail(maybeEmail.get());

      final var isMismatch = requestedUserId != user.getId();
      if (isMismatch) {
        throw new IllegalStateException("Requested identity does not match provided");
      }
    }
  }
}
