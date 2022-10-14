package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserPaymentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  private final StringEncryptor stringEncryptor;

  public UserMapper(StringEncryptor stringEncryptor) {
    this.stringEncryptor = stringEncryptor;
  }

  public EditUserResponseDto applicationUserToEditUserResponseDto(ApplicationUser applicationUser) {
    return new EditUserResponseDto(
        applicationUser.getId(),
        applicationUser.getEmail(),
        applicationUser.getCardOwner(),
        makeSecretResponseString(applicationUser.getCardNumber(), 3),
        applicationUser.getCardExpirationDate(),
        makeSecretResponseString(applicationUser.getCardCvv(), 0));
  }

  public EditUserPaymentDto applicationUserToEditUserPaymentDto(ApplicationUser applicationUser) {
    return new EditUserPaymentDto(
        applicationUser.getCardOwner(),
        makeSecretResponseString(applicationUser.getCardNumber(), 3),
        applicationUser.getCardExpirationDate(),
        makeSecretResponseString(applicationUser.getCardCvv(), 0));
  }

  public String makeSecretResponseString(String input, int leaveLastNumbers) {
    if (input == null) {
      return null;
    }
    input = stringEncryptor.decrypt(input);
    if (leaveLastNumbers == 0) {
      return input.replaceAll("\\d", "*");
    }
    var subSecret = input.substring(0, input.length() - leaveLastNumbers);
    var subShown = input.substring(input.length() - leaveLastNumbers);
    return subSecret.replaceAll("\\d", "*") + subShown;
  }
}
