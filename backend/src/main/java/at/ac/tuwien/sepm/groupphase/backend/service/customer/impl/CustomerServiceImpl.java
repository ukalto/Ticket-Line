package at.ac.tuwien.sepm.groupphase.backend.service.customer.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.customer.RegisterCustomerDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.customer.CustomerService;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerServiceImpl implements CustomerService {
  private final ApplicationUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public RegisterCustomerDto registerCustomer(RegisterCustomerDto customerDto) {
    if (userRepository.findByEmail(customerDto.email()).isPresent()) {
      throw new AlreadyExistsException("Email '" + customerDto.email() + "' is already taken!");
    }

    final var applicationUser =
        new ApplicationUser(
            customerDto.email(),
            passwordEncoder.encode(customerDto.password()),
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    userRepository.save(applicationUser);

    return new RegisterCustomerDto(applicationUser.getEmail(), applicationUser.getPassword());
  }
}
