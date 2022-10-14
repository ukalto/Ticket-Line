package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApplicationUserType {
  ROLE_USER("ROLE_USER"),
  ROLE_ADMINISTRATOR("ROLE_ADMINISTRATOR"),
  ROLE_SUPER_ADMINISTRATOR("ROLE_SUPER_ADMINISTRATOR");

  private final String value;

  ApplicationUserType(final String value) {
    this.value = value;
  }

  @JsonCreator
  public static ApplicationUserType fromString(String value) {
    if (value.equals(ROLE_USER.value)) {
      return ROLE_USER;
    }

    if (value.equals(ROLE_ADMINISTRATOR.value)) {
      return ROLE_ADMINISTRATOR;
    }

    if (value.equals(ROLE_SUPER_ADMINISTRATOR.value)) {
      return ROLE_SUPER_ADMINISTRATOR;
    }

    throw new ValidationException(String.format("%s is not a known role", value));
  }

  @Override
  @JsonValue
  public String toString() {
    return value;
  }
}
