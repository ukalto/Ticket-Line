package at.ac.tuwien.sepm.groupphase.backend.performance.tests;

import at.ac.tuwien.sepm.groupphase.backend.performance.client.Credentials;
import at.ac.tuwien.sepm.groupphase.backend.performance.client.TicketLineClient;
import at.ac.tuwien.sepm.groupphase.backend.performance.executor.TimingExecutor;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateUserTest {
  private record CreateCustomerDto(String email, String password) {}

  private String generateEmail() {
    return String.format("user-%s@gmail.com", UUID.randomUUID());
  }

  @Test
  public void createUser_shouldFinishInUnderOneSecond() {
    final var credentials = new Credentials("admin@gmail.com", "password");
    final var client =
        new TicketLineClient(
            HttpClient.newHttpClient(), credentials, "localhost", 8080, "/api/v1/");
    final var tasks = new ArrayList<Runnable>();

    for (int i = 0; i < 30; i++) {
      tasks.add(
          () -> {
            final var createCustomer = new CreateCustomerDto(generateEmail(), "password");
            try {
              client.doPost("customers", createCustomer, CreateCustomerDto.class);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
    }

    var executor = new TimingExecutor(tasks, 1);
    executor.run();

    var oneSecond = 1 * 1000;
    Assertions.assertTrue(executor.median() < oneSecond);
  }
}
