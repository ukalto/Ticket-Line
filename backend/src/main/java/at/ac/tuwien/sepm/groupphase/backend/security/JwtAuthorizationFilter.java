package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final SecurityProperties securityProperties;
  private final UserService userService;

  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager,
      SecurityProperties securityProperties,
      UserService userService) {
    super(authenticationManager);
    this.securityProperties = securityProperties;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      UsernamePasswordAuthenticationToken authToken = getAuthToken(request);
      if (authToken != null) {
        userService.findApplicationUserByEmail(authToken.getName());
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (IllegalArgumentException | JwtException e) {
      LOGGER.debug("Invalid authorization attempt: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Invalid authorization header or token");
      return;
    } catch (LockedException e) {
      LOGGER.info("Account locked or not found: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Account was locked or deleted!");
      return;
    } catch (BadCredentialsException e) {
      LOGGER.info("Account information is no longer valid: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Session to this account is no longer valid, please login again!");
      return;
    }
    chain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken getAuthToken(HttpServletRequest request)
      throws JwtException, IllegalArgumentException {
    String token = request.getHeader(securityProperties.getAuthHeader());
    if (token == null || token.isEmpty()) {
      return null;
    }

    if (!token.startsWith(securityProperties.getAuthTokenPrefix())) {
      throw new IllegalArgumentException("Authorization header is malformed or missing");
    }

    byte[] signingKey = securityProperties.getJwtSecret().getBytes();

    if (!token.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Token must start with 'Bearer'");
    }
    Claims claims =
        Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();

    String username = claims.getSubject();

    List<SimpleGrantedAuthority> authorities =
        ((List<?>) claims.get("rol"))
            .stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());

    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Token contains no user");
    }

    return new UsernamePasswordAuthenticationToken(username, null, authorities);
  }
}
