package at.ac.tuwien.sepm.groupphase.backend.performance.concurrent;

import at.ac.tuwien.sepm.groupphase.backend.performance.meta.EndpointCaller;

public class NewsQuery implements Runnable {
  private EndpointCaller caller;

  public NewsQuery(final EndpointCaller caller) {
    this.caller = caller;
  }

  @Override
  public void run() {
    this.caller.get_news();
  }
}
