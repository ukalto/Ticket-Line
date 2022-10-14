package at.ac.tuwien.sepm.groupphase.backend.scheduler;

import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventShowingRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ScheduledTasks {

  private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("HH:mm:ss");
  private final EventShowingRepository eventShowingRepository;

  @Scheduled(cron = "0 */5 * * * *")
  public void scheduleTaskWithFixedRate() {
    logger.info(
        "Check reservation deletion for event showings:: Execution Time - {}",
        dateTimeFormatter.format(LocalDateTime.now()));
    eventShowingRepository.deleteAllReservationsHalfAnHourBeforeShowing();
  }
}
