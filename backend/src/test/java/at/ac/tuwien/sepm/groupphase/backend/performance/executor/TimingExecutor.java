package at.ac.tuwien.sepm.groupphase.backend.performance.executor;

import java.util.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class TimingExecutor implements Runnable {
  private final Queue<Runnable> tasks;
  private final List<Long> timings;
  private final int iterations;

  private final List<Long>[] taskTimings;

  public TimingExecutor(final List<Runnable> tasks, final int iterations) {
    this.tasks = new LinkedList<Runnable>(tasks);

    this.iterations = iterations;

    this.timings = new ArrayList<>();
    this.taskTimings = new List[tasks.size()];
  }

  private <T extends Number> double median(List<T> values) {
    final var isEvenlySized = values.size() % 2 == 0;
    final var middleIndex = values.size() / 2;

    if (isEvenlySized) {
      return (values.get(middleIndex).doubleValue() + values.get(middleIndex - 1).doubleValue())
          / 2;
    }

    return values.get(middleIndex).doubleValue();
  }

  @Override
  public void run() {
    for (int i = 0; i < this.iterations; i++) {
      int taskIndex = 0;
      for (final var task : tasks) {
        try {
          var start = System.currentTimeMillis();

          task.run();

          var end = System.currentTimeMillis();
          var delta = end - start;

          timings.add(delta);

          if (this.taskTimings[taskIndex] == null) {
            this.taskTimings[taskIndex] = new ArrayList<>();
          }

          this.taskTimings[taskIndex].add(delta);

          taskIndex++;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public double median() {
    if (timings.size() == 0) {
      throw new IllegalStateException(
          "Cannot retrieve timing data before tasks have been processed.");
    }

    this.timings.sort(Long::compareTo);

    final var filteredTimings =
        this.timings.stream().filter(timing -> timing != 0).collect(Collectors.toList());

    return this.median(filteredTimings);
  }

  public List<Double> timingMedians() {
    final var medians = new ArrayList<Double>();

    for (final var taskTimings : this.taskTimings) {
      final var filtered = taskTimings.stream().filter(timing -> timing != 0).toList();

      if (filtered.size() == 0) {
        medians.add(0.0);
        continue;
      }

      medians.add(this.median(filtered));
    }

    return medians;
  }
}
