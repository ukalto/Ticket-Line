package at.ac.tuwien.sepm.groupphase.backend.unittests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserPaymentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserMappingTest {

  private final ApplicationUser applicationUser =
      new ApplicationUser(
          "testUserAccess@gmail.com",
          "$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO",
          ApplicationUserType.ROLE_USER,
          null,
          false,
          "Test Name",
          "Lqh2iV37EhiANvm31CjiuVWe2XaTZ4Rv",
          "04/23",
          "SsOFgdMO6gj87kxhGt4yRQ==");

  @Autowired private UserMapper userMapper;

  @Test
  public void mapping_applicationUserToEditUserResponseDto() {
    EditUserResponseDto editUserResponseDto =
        userMapper.applicationUserToEditUserResponseDto(applicationUser);
    assertAll(
        () -> assertEquals("testUserAccess@gmail.com", editUserResponseDto.email()),
        () -> assertEquals("Test Name", editUserResponseDto.cardOwner()),
        () -> assertEquals("************123", editUserResponseDto.cardNumber()),
        () -> assertEquals("04/23", editUserResponseDto.cardExpirationDate()),
        () -> assertEquals("****", editUserResponseDto.cardCvv()));
  }

  @Test
  public void mapping_applicationUserToEditUserPaymentDto() {
    EditUserPaymentDto editUserPaymentDto =
        userMapper.applicationUserToEditUserPaymentDto(applicationUser);
    assertAll(
        () -> assertEquals("Test Name", editUserPaymentDto.cardOwner()),
        () -> assertEquals("************123", editUserPaymentDto.cardNumber()),
        () -> assertEquals("04/23", editUserPaymentDto.cardExpirationDate()),
        () -> assertEquals("****", editUserPaymentDto.cardCvv()));
  }
}
