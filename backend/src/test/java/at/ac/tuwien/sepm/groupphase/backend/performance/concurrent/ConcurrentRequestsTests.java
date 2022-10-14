package at.ac.tuwien.sepm.groupphase.backend.performance.concurrent;

import at.ac.tuwien.sepm.groupphase.backend.performance.client.Credentials;
import at.ac.tuwien.sepm.groupphase.backend.performance.client.TicketLineClient;
import at.ac.tuwien.sepm.groupphase.backend.performance.meta.EndpointCaller;
import java.net.http.HttpClient;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConcurrentRequestsTests {
  private final EndpointCaller caller;

  public ConcurrentRequestsTests() {
    final var credentials = new Credentials("admin@gmail.com", "password");
    final var client =
        new TicketLineClient(
            HttpClient.newHttpClient(), credentials, "localhost", 8080, "/api/v1/");

    this.caller = new EndpointCaller(client);
  }

  @Test
  public void oneHundredPaymentRequests_shallBeDoneInUnderOneSecond() {
    final var workers = new ArrayList<Runnable>();
    for (int i = 0; i < 10; i++) {
      final var command = new PaymentRequestCommand(caller);
      final var worker = new Worker(command, 10);

      workers.add(worker);
    }

    final var start = System.currentTimeMillis();
    new ConcurrentExecutor(workers).run();

    final var end = System.currentTimeMillis();
    final var delta = end - start;

    final var oneMinute = 60 * 1000;
    Assertions.assertTrue(delta < oneMinute);
  }

  @Test
  public void oneThousandNewsRequests_shallBeDoneInUnderOneSecond() {
    final var workers = new ArrayList<Runnable>();
    for (int i = 0; i < 10; i++) {
      final var command = new NewsQuery(caller);
      final var worker = new Worker(command, 100);

      workers.add(worker);
    }

    final var start = System.currentTimeMillis();
    new ConcurrentExecutor(workers).run();
    final var end = System.currentTimeMillis();
    final var delta = end - start;

    final var oneMinute = 60 * 1000;
    Assertions.assertTrue(delta < oneMinute);
  }

  @Test
  public void oneThousandEventRequests_shallBeDoneInUnderOneSecond() {
    final var workers = new ArrayList<Runnable>();
    for (int i = 0; i < 10; i++) {
      final var command = new EventQuery(caller);
      final var worker = new Worker(command, 100);

      workers.add(worker);
    }

    final var start = System.currentTimeMillis();
    new ConcurrentExecutor(workers).run();
    final var end = System.currentTimeMillis();
    final var delta = end - start;

    final var oneMinute = 60 * 1000;
    Assertions.assertTrue(delta < oneMinute);
  }
}
