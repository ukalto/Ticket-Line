package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventExpansionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventExpansionDetailsMapper;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventExpansionsDetailRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class EventExpansionsDetailRepositoryTest {
  @Autowired private EventExpansionsDetailRepository repository;
  @Autowired private EventExpansionDetailsMapper eventExpansionDetailsMapper;

  @Test
  void findValidEventsByLocationExistent() {
    List<EventExpansionDetailsDto> result =
        this.repository.findEventsForLocation(-1L).stream()
            .map(eventExpansionDetailsMapper::eventExpansionDetailsToEventExpansionDetailsDto)
            .toList();

    Assertions.assertFalse(result.isEmpty());
    result.forEach(
        event -> {
          if (event.id() == -2L) {
            Assertions.assertEquals("50 Cent Show", event.title());
            Assertions.assertEquals(
                "Today the show will be increased by 1 hour in length!!!", event.description());
            Assertions.assertEquals("Music", event.category());
            Assertions.assertEquals(false, event.soldOut());
          }
        });
  }

  @Test
  void findValidEventsByLocationNonExistent() {
    List<EventExpansionDetailsDto> result =
        this.repository.findEventsForLocation(-999L).stream()
            .map(eventExpansionDetailsMapper::eventExpansionDetailsToEventExpansionDetailsDto)
            .toList();

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void findValidEventsByArtistExistent() {
    List<EventExpansionDetailsDto> result =
        this.repository.findEventsForArtist(-1L).stream()
            .map(eventExpansionDetailsMapper::eventExpansionDetailsToEventExpansionDetailsDto)
            .toList();

    Assertions.assertFalse(result.isEmpty());
    result.forEach(
        event -> {
          if (event.id() == -1L) {
            Assertions.assertEquals("50 Cent Show", event.title());
            Assertions.assertEquals("Today we are going ham!!!", event.description());
            Assertions.assertEquals("Music", event.category());
            Assertions.assertEquals(false, event.soldOut());
          }
        });
  }

  @Test
  void findValidEventsByArtistNonExistent() {
    List<EventExpansionDetailsDto> result =
        this.repository.findEventsForArtist(-999L).stream()
            .map(eventExpansionDetailsMapper::eventExpansionDetailsToEventExpansionDetailsDto)
            .toList();

    Assertions.assertTrue(result.isEmpty());
  }
}
