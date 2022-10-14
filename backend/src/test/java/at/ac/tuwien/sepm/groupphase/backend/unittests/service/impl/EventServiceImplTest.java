package at.ac.tuwien.sepm.groupphase.backend.unittests.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.event.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventShowing;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotCreateEntityException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistPerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingNonSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventShowingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.TopEventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.event.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.event.impl.EventServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.image.impl.ImageServiceImpl;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventServiceImplTest {

  private EventService eventService;
  private EventRepository eventRepository;
  private EventCategoryRepository eventCategoryRepository;
  private EventShowingRepository eventShowingRepository;
  private LocationRepository locationRepository;
  private SeatingPlanRepository seatingPlanRepository;
  private SeatingPlanSectorRepository seatingPlanSectorRepository;
  private SectorPriceEventShowingRepository sectorPriceEventShowingRepository;
  private BookingRepository bookingRepository;
  private BookingNonSeatRepository bookingNonSeatRepository;
  private BookingSeatRepository bookingSeatRepository;
  private ArtistPerformanceRepository artistPerformanceRepository;
  private TopEventRepository topEventRepository;
  private EventMapper eventMapper;
  private ImageServiceImpl imageService;

  @BeforeEach
  void setup() {
    this.eventRepository = mock(EventRepository.class);
    this.topEventRepository = mock(TopEventRepository.class);
    this.eventCategoryRepository = mock(EventCategoryRepository.class);
    this.artistPerformanceRepository = mock(ArtistPerformanceRepository.class);
    this.eventShowingRepository = mock(EventShowingRepository.class);
    this.sectorPriceEventShowingRepository = mock(SectorPriceEventShowingRepository.class);
    this.locationRepository = mock(LocationRepository.class);
    this.seatingPlanRepository = mock(SeatingPlanRepository.class);
    this.seatingPlanSectorRepository = mock(SeatingPlanSectorRepository.class);
    this.bookingRepository = mock(BookingRepository.class);
    this.bookingNonSeatRepository = mock(BookingNonSeatRepository.class);
    this.bookingSeatRepository = mock(BookingSeatRepository.class);
    this.eventMapper = mock(EventMapper.class);
    this.bookingNonSeatRepository = mock(BookingNonSeatRepository.class);
    this.bookingSeatRepository = mock(BookingSeatRepository.class);
    this.bookingRepository = mock(BookingRepository.class);
    this.imageService = mock(ImageServiceImpl.class);

    this.eventService =
        new EventServiceImpl(
            eventRepository,
            eventCategoryRepository,
            eventShowingRepository,
            locationRepository,
            seatingPlanRepository,
            seatingPlanSectorRepository,
            sectorPriceEventShowingRepository,
            bookingRepository,
            bookingNonSeatRepository,
            bookingSeatRepository,
            artistPerformanceRepository,
            topEventRepository,
            eventMapper,
            imageService);
  }

  @Test
  void createEventWithInvalidBody_ThrowsException() {
    EventCreationDto eventCreationDto =
        new EventCreationDto(null, null, null, null, null, null, null);

    Assertions.assertThrows(
        CouldNotCreateEntityException.class, () -> this.eventService.create(eventCreationDto));
  }

  @Test
  void createEventWithValidBody_shouldWork() {
    Timestamp now = Timestamp.valueOf(LocalDateTime.now());

    List<Long> artists = new ArrayList<>();
    artists.add(1L);
    artists.add(2L);
    artists.add(3L);

    List<EventCreationDto.Pricing> pricings = new ArrayList<>();
    List<EventCreationDto.Showing> showings = new ArrayList<>();

    pricings.add(new EventCreationDto.Pricing(1L, new BigDecimal(10)));
    pricings.add(new EventCreationDto.Pricing(2L, new BigDecimal(20)));
    showings.add(new EventCreationDto.Showing(now, 1L, pricings));

    pricings = new ArrayList<>();
    pricings.add(new EventCreationDto.Pricing(3L, new BigDecimal(30)));
    pricings.add(new EventCreationDto.Pricing(4L, new BigDecimal(5)));
    showings.add(new EventCreationDto.Showing(now, 2L, pricings));

    EventCreationDto eventCreationDto =
        new EventCreationDto(
            "Concert",
            -1L,
            new EventCreationDto.Duration(2L, 0L),
            "Music Concert",
            artists,
            showings,
            "test.jpg");

    when(eventRepository.save(any(Event.class)))
        .thenReturn(
            new Event(
                1L,
                eventCreationDto.title(),
                eventCreationDto.categoryId(),
                Duration.ofHours(2),
                eventCreationDto.description(),
                eventCreationDto.imageRef()));

    when(eventShowingRepository.save(any(EventShowing.class)))
        .thenReturn(new EventShowing(1L, now, 1L, 1L));

    Event event = eventService.create(eventCreationDto);

    assertEquals(event.getTitle(), eventCreationDto.title());
    assertEquals(event.getCategoryId(), eventCreationDto.categoryId());
    assertEquals(event.getDescription(), eventCreationDto.description());
  }
}
