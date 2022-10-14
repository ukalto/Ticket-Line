package at.ac.tuwien.sepm.groupphase.backend.performance.concurrent;

import at.ac.tuwien.sepm.groupphase.backend.performance.meta.EndpointCaller;

public class PaymentRequestCommand implements Runnable {
  private final EndpointCaller caller;

  public PaymentRequestCommand(final EndpointCaller caller) {
    this.caller = caller;
  }

  @Override
  public void run() {
    this.caller.post_showing_id_purchase();
  }
}
