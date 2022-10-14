package at.ac.tuwien.sepm.groupphase.backend.unittests.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.service.event.EventService;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventServiceImplTestUnMocked {

  @Autowired private EventService eventService;

  @Test
  void findById_shouldWork() {
    try {
      EventDto event = eventService.findById(-1L).orElse(null);
      assertEquals(event.title(), "50 Cent Show");
      assertEquals(event.categoryId(), -2);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void findById_shouldNotWork() {
    try {
      EventDto event = eventService.findById(5L).orElse(null);
      assertEquals(event, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void findEventCategoryById_shouldWork() {
    EventCategory eventCategory = eventService.findEventCategoryById(-2L).orElse(null);
    assertEquals(eventCategory.getDisplayName(), "Music");
  }

  @Test
  void findEventCategoryById_shouldNotWork() {
    EventCategory eventCategory = eventService.findEventCategoryById(1L).orElse(null);
    assertEquals(eventCategory, null);
  }
}
