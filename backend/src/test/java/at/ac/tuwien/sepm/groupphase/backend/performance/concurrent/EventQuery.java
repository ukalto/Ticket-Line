package at.ac.tuwien.sepm.groupphase.backend.performance.concurrent;

import at.ac.tuwien.sepm.groupphase.backend.performance.meta.EndpointCaller;

public class EventQuery implements Runnable {
  private final EndpointCaller caller;

  public EventQuery(final EndpointCaller caller) {
    this.caller = caller;
  }

  @Override
  public void run() {
    this.caller.get_events();
  }
}
