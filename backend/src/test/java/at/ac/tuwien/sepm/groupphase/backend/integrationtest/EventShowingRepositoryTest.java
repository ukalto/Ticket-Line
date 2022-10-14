package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.entity.EventShowing;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventShowingRepository;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("default")
@SpringBootTest
class EventShowingRepositoryTest {
  @Autowired private EventShowingRepository eventShowingRepository;

  @Test
  public void findAll_shouldFilterShowings() {
    LocalDate date = LocalDate.of(2022, 8, 07);
    LocalTime startTime = LocalTime.of(15, 31, 00);
    LocalTime endTime = LocalTime.of(22, 50, 00);
    BigDecimal maxPrice = BigDecimal.valueOf(10);
    Pageable pageable = PageRequest.of(0, 10);
    Page<EventShowing> showingsPage =
        eventShowingRepository.findFiltered(
            "m", "studio", date, startTime, endTime, null, maxPrice, pageable);
    List<EventShowing> filteredShowings = showingsPage.getContent();

    Assertions.assertEquals(1, filteredShowings.size());
    Assertions.assertEquals(7, filteredShowings.get(0).getId());

    Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.of(2022, 8, 07, 20, 30, 00, 0));

    Assertions.assertEquals(expectedTimestamp, filteredShowings.get(0).getOccursOn());
    Assertions.assertEquals(1, filteredShowings.get(0).getPerformedAt());
    Assertions.assertEquals(4, filteredShowings.get(0).getEventId());
  }
}
