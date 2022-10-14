package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.authentication.AuthenticationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final AuthenticationManager authenticationManager;
  private final JwtTokenizer jwtTokenizer;
  private final UserService userService;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      SecurityProperties securityProperties,
      JwtTokenizer jwtTokenizer,
      UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenizer = jwtTokenizer;
    setFilterProcessesUrl(securityProperties.getLoginUri());
    this.userService = userService;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    AuthenticationRequestDto credentials = null;
    try {
      credentials =
          new ObjectMapper().readValue(request.getInputStream(), AuthenticationRequestDto.class);
      // Compares the user with UserServiceImpl#loadUserByUsername and check if the
      // credentials are correct
      var authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password()));

      this.userService.handleSuccessfulAuthentication(credentials.email());

      return authentication;
    } catch (DataAccessException e) {
      // avoid DataAccessException getting wrongly swallowed by IOException catch clause.
      throw e;
    } catch (IOException e) {
      throw new BadCredentialsException("Wrong API request or JSON schema", e);
    } catch (BadCredentialsException e) {
      if (credentials != null && credentials.email() != null) {
        LOGGER.error("Unsuccessful authentication attempt for user {}", credentials.email());

        this.userService.handleFailedAuthentication(credentials.email());
      }

      throw e;
    }
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(failed.getMessage());
    LOGGER.debug("Invalid authentication attempt: {}", failed.getMessage());
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException {
    var user = ((User) authResult.getPrincipal());
    var applicationUser = this.userService.findApplicationUserByEmail(user.getUsername());

    if (!user.isAccountNonLocked()) {
      throw new LockedException("Account is locked due to many failed login attempts");
    }

    List<String> roles =
        user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    response
        .getWriter()
        .write(jwtTokenizer.getAuthToken(user.getUsername(), roles, applicationUser.getId()));
    LOGGER.info("Successfully authenticated user {}", user.getUsername());
  }
}
