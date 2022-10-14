package at.ac.tuwien.sepm.groupphase.backend.performance.executor;

import at.ac.tuwien.sepm.groupphase.backend.performance.client.Credentials;
import at.ac.tuwien.sepm.groupphase.backend.performance.client.TicketLineClient;
import at.ac.tuwien.sepm.groupphase.backend.performance.meta.EndpointCaller;
import java.net.http.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SuiteExecutor {
  private final TicketLineClient client;
  private final TimingExecutor executor;
  private final EndpointCaller caller;

  public SuiteExecutor() {
    final var credentials = new Credentials("admin@gmail.com", "password");
    this.client =
        new TicketLineClient(
            HttpClient.newHttpClient(), credentials, "localhost", 8080, "/api/v1/");

    this.caller = new EndpointCaller(this.client);
    this.executor = new TimingExecutor(this.caller.asRunnables(), 10);
  }

  @Test
  void runAll() {
    final var generalLimit = 1.5 * 1000;

    this.executor.run();
    final var observedMedian = this.executor.median();

    Assertions.assertTrue(observedMedian < generalLimit);

    final var medians = this.executor.timingMedians();

    medians.forEach(median -> Assertions.assertTrue(median < generalLimit));
  }

  @Test
  void testLongRunning() {
    this.caller.get_locationsEvents();
  }
}
