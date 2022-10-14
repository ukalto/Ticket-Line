package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.sql.Timestamp;
import java.util.Collection;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser")
@Table(name = "application_user")
@NoArgsConstructor
public class ApplicationUser implements UserDetails {
  static final int MAX_AUTHENTICATION_ATTEMPTS = 5;

  public ApplicationUser(
      final String email,
      final String password,
      final ApplicationUserType type,
      final Timestamp memberSince,
      final boolean isBlocked,
      final String cardOwner,
      final String cardNumber,
      final String cardExpirationDate,
      final String cardCvv) {
    this.email = email;
    this.password = password;
    this.type = type;
    this.memberSince = memberSince;
    this.isBlocked = isBlocked;
    this.cardOwner = cardOwner;
    this.cardNumber = cardNumber;
    this.cardExpirationDate = cardExpirationDate;
    this.cardCvv = cardCvv;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ApplicationUserType type;

  @Column(name = "member_since", nullable = false)
  private Timestamp memberSince;

  @Column(name = "is_blocked", nullable = false)
  private boolean isBlocked;

  @Column(name = "failed_authentication_attempts", nullable = false)
  private int failedAuthenticationAttempts;

  @Column(name = "card_owner", nullable = true)
  // only set for type == CUSTOMER
  private String cardOwner;

  @Column(name = "card_number", nullable = true)
  // only set for type == CUSTOMER
  private String cardNumber;

  @Column(name = "card_expiration_date", nullable = true)
  // only set for type == CUSTOMER
  private String cardExpirationDate;

  @Column(name = "card_cvv", nullable = true)
  // only set for type == CUSTOMER
  private String cardCvv;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.isSuperAdministrator()) {
      return AuthorityUtils.createAuthorityList(
          ApplicationUserType.ROLE_SUPER_ADMINISTRATOR.toString(),
          ApplicationUserType.ROLE_ADMINISTRATOR.toString(),
          ApplicationUserType.ROLE_USER.toString());
    }

    if (this.isAdministrator()) {
      return AuthorityUtils.createAuthorityList(
          ApplicationUserType.ROLE_ADMINISTRATOR.toString(),
          ApplicationUserType.ROLE_USER.toString());
    }

    return AuthorityUtils.createAuthorityList(ApplicationUserType.ROLE_USER.toString());
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    var isNoSuperAdminAndExceededAttempts =
        !this.isSuperAdministrator()
            && this.failedAuthenticationAttempts >= MAX_AUTHENTICATION_ATTEMPTS;

    return !(this.isBlocked || isNoSuperAdminAndExceededAttempts);
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public boolean isSuperAdministrator() {
    return this.type == ApplicationUserType.ROLE_SUPER_ADMINISTRATOR;
  }

  public boolean isAdministrator() {
    return this.type == ApplicationUserType.ROLE_ADMINISTRATOR;
  }

  public boolean isApplicationUser() {
    return this.type == ApplicationUserType.ROLE_USER;
  }
}
