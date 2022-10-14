package at.ac.tuwien.sepm.groupphase.backend.performance.concurrent;

public class Worker implements Runnable {
  private final Runnable task;
  private final int repeat;

  public Worker(final Runnable task, final int repeat) {
    this.task = task;
    this.repeat = repeat;
  }

  @Override
  public void run() {
    for (int i = 0; i < this.repeat; i++) {
      this.task.run();
    }
  }
}
