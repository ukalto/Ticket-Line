package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenizer {

  private final SecurityProperties securityProperties;

  public JwtTokenizer(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  private JwtBuilder buildBaseToken(String user) {
    byte[] signingKey = securityProperties.getJwtSecret().getBytes();
    var token =
        Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", securityProperties.getJwtType())
            .setIssuer(securityProperties.getJwtIssuer())
            .setAudience(securityProperties.getJwtAudience())
            .setSubject(user)
            .setExpiration(
                new Date(System.currentTimeMillis() + securityProperties.getJwtExpirationTime()));

    return token;
  }

  public String getAuthToken(String user, List<String> roles, Long userId) {
    var jwt = buildBaseToken(user);

    var encoded = jwt.claim("rol", roles).claim("uid", userId).compact();

    return securityProperties.getAuthTokenPrefix() + encoded;
  }

  public String getPasswordResetToken(String user, Long userId) {
    var roles = List.of("ROLE_PASSWORD_RESET");

    var jwt = buildBaseToken(user);

    var encoded = jwt.claim("rol", roles).claim("uid", userId).compact();

    return securityProperties.getAuthTokenPrefix() + encoded;
  }
}
