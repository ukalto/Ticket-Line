package at.ac.tuwien.sepm.groupphase.backend.unittests.service.impl;

import static org.mockito.Mockito.*;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserAccessRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserPaymentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.Mail;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.MailService;
import at.ac.tuwien.sepm.groupphase.backend.service.mail.PasswordResetLinkGenerator;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.user.impl.UserServiceImpl;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest {
  private ApplicationUserRepository applicationUserRepository;
  private UserService userService;
  private MailService mailService;
  private BCryptPasswordEncoder passwordEncoder;
  private JwtTokenizer jwtTokenizer;
  private PasswordResetLinkGenerator generator;
  private PasswordEncoder encoder;
  private UserMapper userMapper;
  private StringEncryptor stringEncryptor;

  @BeforeEach
  void setup() {
    this.applicationUserRepository = mock(ApplicationUserRepository.class);
    this.mailService = mock(MailService.class);
    this.passwordEncoder = new BCryptPasswordEncoder();
    this.jwtTokenizer = mock(JwtTokenizer.class);
    this.generator = mock(PasswordResetLinkGenerator.class);
    this.encoder = mock(PasswordEncoder.class);
    this.userMapper = mock(UserMapper.class);
    this.jwtTokenizer = mock(JwtTokenizer.class);
    this.stringEncryptor = mock(StringEncryptor.class);

    this.userService =
        new UserServiceImpl(
            this.applicationUserRepository,
            new ApplicationUserMapper(),
            this.passwordEncoder,
            this.mailService,
            this.jwtTokenizer,
            this.generator,
            this.userMapper,
            this.stringEncryptor);
  }

  @Test
  void handleFailedAuthentication_shouldIncreaseFailedAuthenticationAttempts() {
    var user =
        new ApplicationUser(
            "not@relevant.com",
            "",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);
    var originalAttempts = 0;
    user.setFailedAuthenticationAttempts(originalAttempts);

    when(this.applicationUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    this.userService.handleFailedAuthentication(user.getEmail());

    Assertions.assertEquals(originalAttempts + 1, user.getFailedAuthenticationAttempts());
    verify(this.applicationUserRepository, times(1)).save(user);
  }

  @Test
  void handleSuccessfulAuthentication_shouldResetAuthenticationAttempts() {
    var user =
        new ApplicationUser(
            "not@relevant.com",
            "",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    var originalAttempts = 4;
    user.setFailedAuthenticationAttempts(originalAttempts);

    when(this.applicationUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    this.userService.handleSuccessfulAuthentication(user.getEmail());

    Assertions.assertEquals(0, user.getFailedAuthenticationAttempts());
    verify(this.applicationUserRepository, times(1)).save(user);
  }

  @Test
  void updateIsBlockedById_shouldThrow() {
    when(this.applicationUserRepository.findByIdAndTypeIn(
            1L, List.of(ApplicationUserType.ROLE_USER)))
        .thenReturn(Optional.empty());
    Assertions.assertThrows(
        NotFoundException.class,
        () ->
            this.userService.updateIsBlockedById(1L, true, List.of(ApplicationUserType.ROLE_USER)));
  }

  @Test
  void updateIsBlockedById_shouldWork() {
    var applicationUser =
        new ApplicationUser(
            "blockMe@gmail.com",
            "",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    when(this.applicationUserRepository.findByIdAndTypeIn(
            1L, List.of(ApplicationUserType.ROLE_USER)))
        .thenReturn(Optional.of(applicationUser));

    applicationUser.setId(1L);
    applicationUser.setBlocked(true);
    when(this.applicationUserRepository.save(applicationUser)).thenReturn(applicationUser);

    var captor = ArgumentCaptor.forClass(ApplicationUser.class);
    this.userService.updateIsBlockedById(1L, true, List.of(ApplicationUserType.ROLE_USER));
    verify(applicationUserRepository).save(captor.capture());
    var updatedApplicationUser = captor.getValue();

    applicationUser.setBlocked(true);
    Assertions.assertEquals(applicationUser, updatedApplicationUser);
  }

  @Test
  void resetPassword_shouldWork() {
    var applicationUser =
        new ApplicationUser(
            "resetmypassword@gmail.com",
            "old",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    when(this.applicationUserRepository.findById(applicationUser.getId()))
        .thenReturn(Optional.of(applicationUser));

    final var newPassword = "new";

    this.userService.resetPassword(applicationUser.getId(), newPassword);
    Assertions.assertTrue(this.passwordEncoder.matches(newPassword, applicationUser.getPassword()));
  }

  @Test
  void triggerPasswordReset_shouldWork() {
    var applicationUser =
        new ApplicationUser(
            "resetmypassword@gmail.com",
            "old",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    when(this.applicationUserRepository.findById(applicationUser.getId()))
        .thenReturn(Optional.of(applicationUser));

    var resetJwt = "iamaresetjwt";
    var resetLink = "https://somelink.com/reset?jwt=" + resetJwt;

    when(this.jwtTokenizer.getPasswordResetToken(
            applicationUser.getUsername(), applicationUser.getId()))
        .thenReturn(resetJwt);
    when(this.generator.forResetJwt(resetJwt)).thenReturn(resetLink);

    this.userService.triggerPasswordReset(applicationUser.getId());

    verify(this.jwtTokenizer)
        .getPasswordResetToken(applicationUser.getUsername(), applicationUser.getId());

    var captor = ArgumentCaptor.forClass(Mail.class);
    verify(this.mailService).send(captor.capture());

    var mail = captor.getValue();
    Assertions.assertTrue(mail.getBody().contains(resetLink));
  }

  void updateUserAccess_shouldThrowAlreadyExists() {
    var editUserAccessRequestDto = new EditUserAccessRequestDto("testUserAccess@gmail.com", "", "");

    ApplicationUser applicationUser1 = new ApplicationUser();
    applicationUser1.setId(0L);
    when(applicationUserRepository.findById(0L)).thenReturn(Optional.of(applicationUser1));

    ApplicationUser applicationUser2 = new ApplicationUser();
    applicationUser2.setId(1L);
    when(applicationUserRepository.findByEmail(editUserAccessRequestDto.email()))
        .thenReturn(Optional.of(applicationUser2));

    Assertions.assertThrows(
        AlreadyExistsException.class,
        () -> this.userService.updateUserAccess(0L, editUserAccessRequestDto));
  }

  @Test
  void updateUserAccess_shouldThrowNotFound() {
    var editUserAccessRequestDto = new EditUserAccessRequestDto("testUserAccess@gmail.com", "", "");

    when(applicationUserRepository.findById(0L)).thenReturn(Optional.empty());

    Assertions.assertThrows(
        NotFoundException.class,
        () -> this.userService.updateUserAccess(0L, editUserAccessRequestDto));
  }

  @Test
  void updateUserAccess_shouldThrowBadRequest() {
    var editUserAccessRequestDto =
        new EditUserAccessRequestDto("testUserAccess@gmail.com", "notThePassword", "newPassword");

    when(applicationUserRepository.findById(-16L))
        .thenReturn(
            Optional.of(
                new ApplicationUser(
                    "testUserAccess@gmail.com",
                    "$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO",
                    ApplicationUserType.ROLE_USER,
                    null,
                    false,
                    null,
                    null,
                    null,
                    null)));

    Assertions.assertThrows(
        BadCredentialsException.class,
        () -> this.userService.updateUserAccess(-16L, editUserAccessRequestDto));
  }

  @Test
  void updateUserAccess_shouldWork() {
    var editUserAccessRequestDto =
        new EditUserAccessRequestDto("testUserAccessNEW@gmail.com", "password", "newPassword");

    var applicationUser =
        new ApplicationUser(
            "testUserAccess@gmail.com",
            "$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO",
            ApplicationUserType.ROLE_USER,
            null,
            false,
            null,
            null,
            null,
            null);
    var result =
        new ApplicationUser(
            "testUserAccessNEW@gmail.com",
            "newHashedPassword",
            ApplicationUserType.ROLE_USER,
            null,
            false,
            null,
            null,
            null,
            null);

    when(applicationUserRepository.findById(-16L)).thenReturn(Optional.of(applicationUser));

    when(encoder.matches(
            "password", "$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO"))
        .thenReturn(true);

    when(encoder.encode("newPassword")).thenReturn("newHashedPassword");

    when(applicationUserRepository.save(applicationUser)).thenReturn(result);

    String token = this.userService.updateUserAccess(-16L, editUserAccessRequestDto);
    Assertions.assertNull(token);
  }

  @Test
  void updateUserPayment_shouldThrowNotFound() {
    var editUserPaymentDto =
        new EditUserPaymentDto("Test Name", "1234567890123456", "04/23", "1234");

    when(applicationUserRepository.findById(0L)).thenReturn(Optional.empty());

    Assertions.assertThrows(
        NotFoundException.class,
        () -> this.userService.updateUserPayment(0L, editUserPaymentDto, false));
  }

  @Test
  void updateUserPayment_shouldWorkRemovePayment() {
    var editUserPaymentDto =
        new EditUserPaymentDto("Test Name", "1234567890123456", "04/23", "1234");

    when(applicationUserRepository.findById(0L)).thenReturn(Optional.of(new ApplicationUser()));

    when(applicationUserRepository.save(new ApplicationUser())).thenReturn(new ApplicationUser());

    this.userService.updateUserPayment(0L, editUserPaymentDto, true);
  }

  @Test
  void updateUserPayment_shouldWorkUpdatePayment() {
    var editUserPaymentDto =
        new EditUserPaymentDto("Test Name", "1234567890123456", "04/23", "1234");

    var result = new EditUserPaymentDto("Test Name", "*************456", "04/23", "****");

    var applicationUser = new ApplicationUser();
    applicationUser.setCardOwner("Test Name");
    applicationUser.setCardNumber("1234567890123456");
    applicationUser.setCardOwner("04/23");
    applicationUser.setCardOwner("1234");

    var encryptedApplicationUser = new ApplicationUser();
    applicationUser.setCardOwner("Test Name");
    applicationUser.setCardNumber("encryptedCardNumber");
    applicationUser.setCardOwner("04/23");
    applicationUser.setCardOwner("encryptedCvv");

    when(applicationUserRepository.findById(0L)).thenReturn(Optional.of(new ApplicationUser()));

    when(stringEncryptor.encrypt("1234567890123456")).thenReturn("encryptedCardNumber");
    when(stringEncryptor.encrypt("1234")).thenReturn("encryptedCvv");
    when(applicationUserRepository.save(encryptedApplicationUser))
        .thenReturn(encryptedApplicationUser);
    when(userMapper.applicationUserToEditUserPaymentDto(encryptedApplicationUser))
        .thenReturn(result);

    var endResult = this.userService.updateUserPayment(0L, editUserPaymentDto, true);

    Assertions.assertEquals(result, endResult);
  }
}
