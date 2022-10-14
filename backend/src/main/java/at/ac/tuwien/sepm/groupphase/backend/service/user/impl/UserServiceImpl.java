package at.ac.tuwien.sepm.groupphase.backend.service.user.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserAccessRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserPaymentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.MailService;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.PasswordResetLinkGenerator;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.PasswordResetMail;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceImpl implements UserService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ApplicationUserRepository userRepository;
  private final ApplicationUserMapper applicationUserMapper;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;
  private final JwtTokenizer tokenizer;
  private final PasswordResetLinkGenerator generator;
  private final UserMapper userMapper;
  private final StringEncryptor stringEncryptor;

  @Override
  public void triggerPasswordReset(Long id) {
    var maybeUser = this.userRepository.findById(id);
    if (maybeUser.isEmpty()) {
      throw new NotFoundException(String.format("User does not exist", id));
    }

    var user = maybeUser.get();

    var resetJwt = tokenizer.getPasswordResetToken(user.getUsername(), user.getId());
    this.mailService.send(new PasswordResetMail(this.generator, user.getEmail(), resetJwt));
  }

  @Override
  public void resetPassword(Long id, String newPassword) {
    var maybeUser = this.userRepository.findById(id);
    if (maybeUser.isEmpty()) {
      throw new NotFoundException(String.format("User does not exist", id));
    }

    var user = maybeUser.get();

    user.setPassword(passwordEncoder.encode(newPassword));

    this.userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    LOGGER.debug("Load all user by email");
    try {
      var applicationUser = findApplicationUserByEmail(email);
      return new User(
          applicationUser.getEmail(),
          applicationUser.getPassword(),
          applicationUser.getAuthorities());
    } catch (NotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage(), e);
    }
  }

  @Override
  public ApplicationUser findApplicationUserByEmail(String email) {
    LOGGER.debug("Find application user by email");
    final var maybeApplicationUser = userRepository.findByEmail(email);
    if (maybeApplicationUser.isPresent()) {
      if (maybeApplicationUser.get().isBlocked()) {
        throw new LockedException(
            "This Account was locked due to many failed login attempts or by an administrator!");
      }
      return maybeApplicationUser.get();
    }
    throw new BadCredentialsException("Bad Credentials");
  }

  @Override
  public Page<ApplicationUserDto> findAllUsersByRoleAndEmail(
      Collection<ApplicationUserType> roles, String email, Pageable pageable) {
    LOGGER.debug("Find application users by role and email");
    System.out.println(pageable.getPageNumber());
    return userRepository
        .findByTypeInAndEmailContaining(roles, email, pageable)
        .map(applicationUserMapper::entityToResponseDto);
  }

  @Override
  public ApplicationUserDto updateIsBlockedById(
      Long id, boolean newIsBlocked, Collection<ApplicationUserType> roles) {
    final var applicationUser = userRepository.findByIdAndTypeIn(id, roles);
    if (applicationUser.isPresent()) {
      applicationUser.get().setBlocked(newIsBlocked);
      return applicationUserMapper.entityToResponseDto(userRepository.save(applicationUser.get()));
    }
    throw new NotFoundException("User does not exist!");
  }

  @Override
  public void handleFailedAuthentication(String email) {
    var applicationUser = this.findApplicationUserByEmail(email);
    applicationUser.setFailedAuthenticationAttempts(
        applicationUser.getFailedAuthenticationAttempts() + 1);
    if (!applicationUser.isAccountNonLocked()) {
      applicationUser.setBlocked(true);
    }
    this.userRepository.save(applicationUser);
  }

  @Override
  public void handleSuccessfulAuthentication(String email) {
    var applicationUser = this.findApplicationUserByEmail(email);
    applicationUser.setFailedAuthenticationAttempts(0);
    this.userRepository.save(applicationUser);
  }

  @Override
  public String updateUserAccess(Long id, EditUserAccessRequestDto editUserAccessRequestDto) {

    final var applicationUser = this.userRepository.findById(id);
    if (applicationUser.isEmpty()) {
      throw new NotFoundException("This user does not exist!");
    }

    final var userWithEmail = this.userRepository.findByEmail(editUserAccessRequestDto.email());
    if (userWithEmail.isPresent()
        && !userWithEmail.get().getId().equals(applicationUser.get().getId())) {
      throw new AlreadyExistsException(
          "Failed to update email address: Email '"
              + editUserAccessRequestDto.email()
              + "' is already taken. Please choose a different email address.");
    }

    boolean newPasswordEntered =
        editUserAccessRequestDto.password() != null
            && editUserAccessRequestDto.currentPassword() != null
            && !editUserAccessRequestDto.password().isBlank()
            && !editUserAccessRequestDto.currentPassword().isBlank();

    if (newPasswordEntered) {
      if (passwordEncoder.matches(
          editUserAccessRequestDto.currentPassword(), applicationUser.get().getPassword())) {
        applicationUser
            .get()
            .setPassword(passwordEncoder.encode(editUserAccessRequestDto.password()));
      } else {
        throw new BadCredentialsException(
            "Failed to change password: Current password was incorrect. "
                + "Please try again to change your password.");
      }
    }
    applicationUser.get().setEmail(editUserAccessRequestDto.email());
    var newApplicationUser = this.userRepository.save(applicationUser.get());

    List<String> roles =
        newApplicationUser.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return this.tokenizer.getAuthToken(
        newApplicationUser.getUsername(), roles, newApplicationUser.getId());
  }

  @Override
  public EditUserPaymentDto updateUserPayment(
      Long id, EditUserPaymentDto editUserPaymentDto, boolean removePayment) {
    final var applicationUser = this.userRepository.findById(id);
    if (applicationUser.isEmpty()) {
      throw new NotFoundException("This user does not exist!");
    }
    var user = applicationUser.get();
    if (removePayment) {
      user.setCardOwner(null);
      user.setCardNumber(null);
      user.setCardExpirationDate(null);
      user.setCardCvv(null);
    } else {

      if (editUserPaymentDto.cardOwner() != null && !editUserPaymentDto.cardOwner().isBlank()) {
        user.setCardOwner(editUserPaymentDto.cardOwner());
      }
      if (editUserPaymentDto.cardNumber() != null && !editUserPaymentDto.cardNumber().isBlank()) {
        user.setCardNumber(stringEncryptor.encrypt(editUserPaymentDto.cardNumber()));
      }
      if (editUserPaymentDto.cardExpirationDate() != null
          && !editUserPaymentDto.cardExpirationDate().isBlank()) {
        validateExpirationDate(editUserPaymentDto.cardExpirationDate());
        user.setCardExpirationDate(editUserPaymentDto.cardExpirationDate());
      }
      if (editUserPaymentDto.cardCvv() != null && !editUserPaymentDto.cardCvv().isBlank()) {
        user.setCardCvv(stringEncryptor.encrypt(editUserPaymentDto.cardCvv()));
      }
    }
    return userMapper.applicationUserToEditUserPaymentDto(userRepository.save(user));
  }

  @Override
  public boolean paymentInfoExists(Long userId) {
    Optional<ApplicationUser> optionalUser = userRepository.findById(userId);

    if (!optionalUser.isPresent()) {
      throw new NotFoundException("Referenced user does not exist");
    }

    ApplicationUser user = optionalUser.get();

    if (user.getCardNumber() == null
        || user.getCardExpirationDate() == null
        || user.getCardCvv() == null
        || user.getCardOwner() == null) {
      return false;
    }

    return true;
  }

  @Override
  public void validateExpirationDate(String expirationDate) {
    int month = Integer.parseInt(expirationDate.substring(0, 2));

    DateTimeFormatter twoYearFormatter = DateTimeFormatter.ofPattern("yy");
    int year = twoYearFormatter.parse(expirationDate.substring(3, 5)).get(ChronoField.YEAR);

    LocalDate localDateExpiration = LocalDate.of(year, month, 1);
    LocalDate now = LocalDate.now();
    now = LocalDate.of(now.getYear(), now.getMonth(), 1);

    if (localDateExpiration.compareTo(now) < 0) {
      throw new ValidationException("Card is expired");
    }
  }

  @Override
  public void deleteUser(Long userId) {
    LOGGER.info("Deleting {}", userId);
    userRepository.deleteUserAndReservations(userId);
  }
}
