package at.ac.tuwien.sepm.groupphase.backend.performance.concurrent;

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;

public class ConcurrentExecutor implements Runnable {
  private final List<Runnable> tasks;

  public ConcurrentExecutor(final List<Runnable> tasks) {
    this.tasks = tasks;
  }

  @SneakyThrows
  @Override
  public void run() {
    final var threads = new ArrayList<Thread>();

    this.tasks.forEach(task -> threads.add(new Thread(task)));

    threads.forEach(Thread::start);

    for (Thread thread : threads) {
      thread.join();
    }
  }
}
