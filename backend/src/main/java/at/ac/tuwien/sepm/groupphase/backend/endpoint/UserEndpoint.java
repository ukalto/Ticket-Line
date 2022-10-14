package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import at.ac.tuwien.sepm.groupphase.backend.security.IdentityAsserter;
import at.ac.tuwien.sepm.groupphase.backend.service.booking.BookingService;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);
  private final UserService userService;
  private final BookingService bookingService;
  private final IdentityAsserter identityAsserter;
  private final UserMapper userMapper;

  @Secured("ROLE_ADMINISTRATOR")
  @GetMapping("/users/admin")
  @Operation(
      summary = "Lists all users in the system",
      security = @SecurityRequirement(name = "apiKey"))
  public Page<ApplicationUserDto> findAllCustomersByName(
      @RequestParam(required = false) String email,
      @RequestParam int page,
      @RequestParam int size) {
    LOGGER.info("GET /api/v1/users/admin with email: {}", email);
    Collection<ApplicationUserType> roles = new ArrayList<>(List.of(ApplicationUserType.ROLE_USER));
    return userService.findAllUsersByRoleAndEmail(roles, email, PageRequest.of(page, size));
  }

  @Secured("ROLE_SUPER_ADMINISTRATOR")
  @GetMapping("/users/super-admin")
  @Operation(
      summary = "Lists all users in the system",
      security = @SecurityRequirement(name = "apiKey"))
  public Page<ApplicationUserDto> findAllAdminsByName(
      @RequestParam(required = false) String email,
      @RequestParam int page,
      @RequestParam int size) {
    LOGGER.info("GET /api/v1/users/super-admin with email: {}", email);
    Collection<ApplicationUserType> roles =
        new ArrayList<>(
            Arrays.asList(ApplicationUserType.ROLE_ADMINISTRATOR, ApplicationUserType.ROLE_USER));
    return userService.findAllUsersByRoleAndEmail(roles, email, PageRequest.of(page, size));
  }

  @Secured("ROLE_USER")
  @GetMapping("/user")
  @Operation(summary = "Find user by email", security = @SecurityRequirement(name = "apiKey"))
  public EditUserResponseDto findUserByEmail(@RequestParam String email) {
    LOGGER.info("GET /api/v1/user with email: {}", email);
    return this.userMapper.applicationUserToEditUserResponseDto(
        this.userService.findApplicationUserByEmail(email));
  }

  @Secured("ROLE_ADMINISTRATOR")
  @PatchMapping("/user/customer/{id}")
  @Operation(
      summary = "Blocks / unblocks specified customer",
      security = @SecurityRequirement(name = "apiKey"))
  public ApplicationUserDto updateCustomerIsBlocked(
      @PathVariable Long id, @RequestBody boolean isBlocked) {
    LOGGER.info("PATCH /api/v1/user/customer/{}", id);
    Collection<ApplicationUserType> roles = new ArrayList<>(List.of(ApplicationUserType.ROLE_USER));
    return this.userService.updateIsBlockedById(id, isBlocked, roles);
  }

  @Secured("ROLE_SUPER_ADMINISTRATOR")
  @PatchMapping("/user/admin/{id}")
  @Operation(
      summary = "Blocks / unblocks specified administrator",
      security = @SecurityRequirement(name = "apiKey"))
  public ApplicationUserDto updateAdminIsBlocked(
      @PathVariable Long id, @RequestBody boolean isBlocked) {
    LOGGER.info("PATCH /api/v1/user/admin/{}", id);
    Collection<ApplicationUserType> roles =
        new ArrayList<>(
            Arrays.asList(ApplicationUserType.ROLE_ADMINISTRATOR, ApplicationUserType.ROLE_USER));
    return this.userService.updateIsBlockedById(id, isBlocked, roles);
  }

  // As this only triggers a mail and no actual reset
  // it is unproblematic to expose.
  @PermitAll()
  @PostMapping("/user/{id}/password-reset")
  @Operation(summary = "Triggers a password reset for a given user")
  public void triggerPasswordReset(@PathVariable Long id) {
    LOGGER.info("POST /api/v1/user/{}/password-reset", id);

    this.userService.triggerPasswordReset(id);
  }

  @PermitAll()
  @PostMapping("/users/password-reset")
  @Operation(summary = "Triggers a password reset for a given user by mail")
  public void triggerPasswordResetByMail(@RequestBody ResetPasswordByEmailDto resetDto) {
    LOGGER.info("POST /api/v1/users/password-reset resets password of: {}", resetDto.email());

    var email = resetDto.email();
    var applicationUser = this.userService.findApplicationUserByEmail(email);
    this.userService.triggerPasswordReset(applicationUser.getId());
  }

  @Secured({"ROLE_PASSWORD_RESET", "ROLE_USER", "ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMIN"})
  @PatchMapping("/user/{id}")
  @Operation(summary = "Resets the password for a given user")
  public void resetPassword(
      @PathVariable Long id,
      @RequestBody ResetPasswordDto resetPasswordDto,
      @RequestHeader("Authorization") String authorizationHeader) {
    LOGGER.info("PATCH /api/v1/user/{}", id);

    this.identityAsserter.assertMatch(id, authorizationHeader);

    this.userService.resetPassword(id, resetPasswordDto.newPassword());
  }

  @Secured("ROLE_USER")
  @PatchMapping("/user/{id}/access")
  @Operation(
      summary = "Updates users access information",
      security = @SecurityRequirement(name = "apiKey"))
  public String updateUserAccess(
      @PathVariable Long id, @RequestBody EditUserAccessRequestDto editUserAccessDto) {
    LOGGER.info("PATCH /api/v1/user/{}/access", id);

    return this.userService.updateUserAccess(id, editUserAccessDto);
  }

  @PermitAll
  @GetMapping("/user/{id}/bookings")
  @Operation(
      summary = "Gets all bookings for one user",
      security = @SecurityRequirement(name = "apiKey"))
  public List<BookingOverviewDto> findAllBookingsByBookedBy(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/user/{}/bookings", id);
    return this.bookingService.findAllBookingsByBookedBy(id);
  }

  @Secured("ROLE_USER")
  @PatchMapping("/user/{id}/payment")
  @Operation(
      summary = "Updates users payment information",
      security = @SecurityRequirement(name = "apiKey"))
  public EditUserPaymentDto updateUserPayment(
      @PathVariable Long id,
      @RequestBody EditUserPaymentDto editUserPaymentDto,
      @RequestParam boolean removePayment) {
    LOGGER.info("PATCH /api/v1/user/{}/payment", id);

    return this.userService.updateUserPayment(id, editUserPaymentDto, removePayment);
  }

  @Secured("ROLE_USER")
  @GetMapping("user/{id}/payment")
  @Operation(
      summary = "Checks if user has payment information saved",
      security = @SecurityRequirement(name = "apiKey"))
  public void paymentInfoExists(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/user/{}/payment", id);

    if (!this.userService.paymentInfoExists(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  @Secured("ROLE_USER")
  @DeleteMapping("user/{id}")
  @Operation(summary = "Deletes user if exists", security = @SecurityRequirement(name = "apiKey"))
  public void deleteUser(@PathVariable Long id) {
    LOGGER.info("DELETE /api/v1/user/{}", id);

    this.userService.deleteUser(id);
  }
}
