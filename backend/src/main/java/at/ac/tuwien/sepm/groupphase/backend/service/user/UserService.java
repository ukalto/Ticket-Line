package at.ac.tuwien.sepm.groupphase.backend.service.user;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserAccessRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserPaymentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
  /**
   * Triggers a password reset for a given user. Will send a mail using the MailService containing a
   * link to the frontend page where a user can perform a password reset.
   *
   * @param id of the user to reset the password for.
   */
  void triggerPasswordReset(Long id);

  /**
   * Resets the password for a specific user.
   *
   * @param id of the user to reset the password for.
   * @param newPassword is the new password of the user.
   */
  void resetPassword(Long id, String newPassword);

  /**
   * Find a user in the context of Spring Security based on the email address <br>
   * For more information have a look at this tutorial:
   * https://www.baeldung.com/spring-security-authentication-with-a-database
   *
   * @param email the email address
   * @return a Spring Security user
   * @throws UsernameNotFoundException is thrown if the specified user does not exist.
   */
  @Override
  UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

  /**
   * Find an application user based on the email address.
   *
   * @param email the email address
   * @return an application user
   */
  ApplicationUser findApplicationUserByEmail(String email);

  /**
   * Find application users that are one of the defined roles and match mail.
   *
   * @param roles contains roles that the user must be of
   * @param email email that is matched with
   * @param pageable defines pagination
   * @return a list of application users
   */
  Page<ApplicationUserDto> findAllUsersByRoleAndEmail(
      Collection<ApplicationUserType> roles, String email, Pageable pageable);

  /**
   * Updates isBlocked state of specified user.
   *
   * @param id identifier of the user
   * @param newIsBlocked new blocked status of the user
   * @param roles contains roles that can be updated
   * @return an ApplicationUserDto of the updated user
   */
  ApplicationUserDto updateIsBlockedById(
      Long id, boolean newIsBlocked, Collection<ApplicationUserType> roles);

  /**
   * Increases the failed authentication attempts of a given user and blocks him / her in case the
   * maximum has been exceeded.
   *
   * @param email of the user that has tried to authenticate unsuccessfully.
   */
  void handleFailedAuthentication(String email);

  /**
   * Resets failed authentication attempts on successful authentication.
   *
   * @param email of the user to reset failed authentication attempts for.
   */
  void handleSuccessfulAuthentication(String email);

  /**
   * Updates the users access information.
   *
   * @param id identifier of the user
   * @param editUserAccessRequestDto access information to be patched
   * @return email of the updated user
   */
  String updateUserAccess(Long id, EditUserAccessRequestDto editUserAccessRequestDto);

  /**
   * Updates the users payment information.
   *
   * @param id identifier of the user
   * @param editUserPaymentDto payment information to be patched
   * @param removePayment flag that defines if payment shall be completely removed
   * @return new paymentInformation
   */
  EditUserPaymentDto updateUserPayment(
      Long id, EditUserPaymentDto editUserPaymentDto, boolean removePayment);

  /**
   * Checks whether or not a customer has payment information saved.
   *
   * @param userId of customer who's payment information is being checked
   * @return true if payment information exists and false if not
   */
  boolean paymentInfoExists(Long userId);

  /**
   * Checks whether or not an expiration date lies in the past.
   *
   * @param expirationDate being checked
   */
  void validateExpirationDate(String expirationDate);

  /**
   * Deletes the user with the specified identifier.
   *
   * @param userId unique identifier of a user
   */
  void deleteUser(Long userId);
}
