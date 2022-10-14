package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ApplicationUserRepositoryTest {
  @Autowired private ApplicationUserRepository repository;

  @Test
  void findOneForNoneExistingUser_shouldNotThrow() {
    var maybeUser = this.repository.findByEmail("somenoneexisting@mail.com");
    Assertions.assertTrue(maybeUser.isEmpty());
  }

  @Test
  void findExistingUser_shouldReturnUser() {
    var expectedUser =
        new ApplicationUser(
            "email@email.com",
            "password",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    this.repository.save(expectedUser);

    var maybeUser = this.repository.findByEmail(expectedUser.getEmail());
    Assertions.assertTrue(maybeUser.isPresent());

    Assertions.assertEquals(expectedUser, maybeUser.get());
  }

  @Test
  void findByIdAndTypeIn_shouldNotThrow() {
    var applicationUser =
        this.repository.findByIdAndTypeIn(-10000L, List.of(ApplicationUserType.ROLE_USER));
    Assertions.assertTrue(applicationUser.isEmpty());
  }

  @Test
  void findByIdAndTypeIn_shouldReturnUser() {
    var expectedUser =
        new ApplicationUser(
            "test@gmail.com",
            "password",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false,
            null,
            null,
            null,
            null);

    this.repository.save(expectedUser);
    var id = this.repository.findByEmail("test@gmail.com").get().getId();
    var applicationUser =
        this.repository.findByIdAndTypeIn(id, List.of(ApplicationUserType.ROLE_USER));
    Assertions.assertTrue(applicationUser.isPresent());
    Assertions.assertEquals(expectedUser, applicationUser.get());
  }

  @Test
  void findTop10ByTypeInAndEmailContaining_shouldNotThrow() {
    var applicationUser =
        this.repository.findByTypeInAndEmailContaining(
            List.of(ApplicationUserType.ROLE_USER), "nonexistence", PageRequest.of(0, 10));
    Assertions.assertTrue(applicationUser.isEmpty());
  }

  /*  @Test
  void findTop10ByTypeInAndEmailContaining_shouldReturnUsers() {
    var expectedUser1 =
        new ApplicationUser(
            "test1@gmail.com",
            "password1",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false);
    var expectedUser2 =
        new ApplicationUser(
            "test2@gmail.com",
            "password2",
            ApplicationUserType.ROLE_USER,
            Timestamp.from(Instant.now()),
            false);

    this.repository.save(expectedUser1);
    this.repository.save(expectedUser2);
    var applicationUsers =
        this.repository.findByTypeInAndEmailContaining(
            List.of(ApplicationUserType.ROLE_USER, ApplicationUserType.ROLE_ADMINISTRATOR),
            "test",
            PageRequest.of(0, 10));
    Assertions.assertTrue(applicationUsers.contains(expectedUser1));
    Assertions.assertTrue(applicationUsers.contains(expectedUser2));
  }*/

  @Test
  @DirtiesContext
  public void save_shouldUpdatePaymentInformation() {
    ApplicationUser applicationUser = repository.getById(-1L);

    applicationUser.setCardNumber("1234567891011121");
    applicationUser.setCardExpirationDate("01/22");
    applicationUser.setCardCvv("123");
    applicationUser.setCardOwner("Max Mustermann");

    repository.save(applicationUser);

    ApplicationUser updatedUser = repository.getById(-1L);
    Assertions.assertEquals(applicationUser.getId(), updatedUser.getId());
    Assertions.assertEquals("1234567891011121", updatedUser.getCardNumber());
    Assertions.assertEquals("01/22", updatedUser.getCardExpirationDate());
    Assertions.assertEquals("123", updatedUser.getCardCvv());
    Assertions.assertEquals("Max Mustermann", updatedUser.getCardOwner());
  }
}
