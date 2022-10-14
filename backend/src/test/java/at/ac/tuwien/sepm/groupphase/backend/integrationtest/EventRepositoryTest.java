package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class EventRepositoryTest {

  @Autowired private EventRepository eventRepository;

  @Test
  void findEventsByTitleExistent() {
    List<Event> result = this.eventRepository.findTop3ByTitleContainingAllIgnoreCase("50 Cent");

    Assertions.assertFalse(result.isEmpty());
    Assertions.assertEquals(2, result.size());

    result.forEach(
        event -> {
          Assertions.assertEquals(event.getTitle(), "50 Cent Show");
          Assertions.assertEquals(event.getCategoryId(), -2L);
          Assertions.assertTrue(event.getDescription().contains("Today"));
          Assertions.assertEquals(event.getDuration().toString(), "PT0.00003S");
        });
  }

  @Test
  void findEventsByTitleNonExistent() {
    List<Event> result =
        this.eventRepository.findTop3ByTitleContainingAllIgnoreCase("asdshkfhgwerasd");
    Assertions.assertTrue(result.isEmpty());
  }
}
