package at.ac.tuwien.sepm.groupphase.backend.service.administrator.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.administrator.CreateAdministratorDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.administrator.AdministratorService;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl implements AdministratorService {
  private final ApplicationUserRepository applicationUserRepository;
  private final PasswordEncoder passwordEncoder;

  public AdministratorServiceImpl(
      final ApplicationUserRepository repository, final PasswordEncoder passwordEncoder) {
    this.applicationUserRepository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public CreateAdministratorDto create(CreateAdministratorDto administratorDto) {
    if (this.applicationUserRepository.findByEmail(administratorDto.email()).isPresent()) {
      throw new AlreadyExistsException("Email '" + administratorDto.email() + "'is already taken!");
    }

    final var applicationUser =
        new ApplicationUser(
            administratorDto.email(),
            passwordEncoder.encode(administratorDto.password()),
            administratorDto.type(),
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    this.applicationUserRepository.save(applicationUser);

    return new CreateAdministratorDto(
        applicationUser.getEmail(), applicationUser.getPassword(), administratorDto.type());
  }
}
