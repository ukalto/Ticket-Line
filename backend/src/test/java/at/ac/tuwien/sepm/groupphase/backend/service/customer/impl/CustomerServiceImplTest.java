package at.ac.tuwien.sepm.groupphase.backend.service.customer.impl;

import static org.mockito.Mockito.*;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.customer.RegisterCustomerDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.customer.CustomerService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class CustomerServiceImplTest {
  private CustomerService service;
  private ApplicationUserRepository repository;
  private PasswordEncoder encoder;

  @BeforeEach
  void setup() {
    this.repository = mock(ApplicationUserRepository.class);
    this.encoder = mock(PasswordEncoder.class);
    this.service = new CustomerServiceImpl(repository, encoder);
  }

  @Test
  void registerCustomerWithExistingEmail_shouldThrow() {
    var registerCustomerDto = new RegisterCustomerDto("some@email.com", "password");

    when(repository.findByEmail(registerCustomerDto.email()))
        .thenReturn(Optional.of(new ApplicationUser()));

    Assertions.assertThrows(
        AlreadyExistsException.class, () -> this.service.registerCustomer(registerCustomerDto));
  }

  @Test
  void registerCustomerWithUniqueEmail_shouldWork() {
    var registerCustomerDto = new RegisterCustomerDto("some@email.com", "password");
    var encodedExpectedPassword = "encodedpassword";

    when(repository.findByEmail(registerCustomerDto.email())).thenReturn(Optional.empty());
    when(encoder.encode(registerCustomerDto.password())).thenReturn(encodedExpectedPassword);

    var captor = ArgumentCaptor.forClass(ApplicationUser.class);

    this.service.registerCustomer(registerCustomerDto);

    verify(repository).save(captor.capture());

    var createdUser = captor.getValue();

    Assertions.assertEquals(registerCustomerDto.email(), createdUser.getEmail());
    Assertions.assertEquals(encodedExpectedPassword, createdUser.getPassword());
    Assertions.assertEquals(ApplicationUserType.ROLE_USER, createdUser.getType());
    Assertions.assertNotNull(createdUser.getMemberSince());
  }
}
