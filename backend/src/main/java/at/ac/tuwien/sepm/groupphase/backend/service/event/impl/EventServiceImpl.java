package at.ac.tuwien.sepm.groupphase.backend.service.event.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailsShowsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.event.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotCreateEntityException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistPerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorPriceAndName;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingNonSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.event.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.image.impl.ImageServiceImpl;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class EventServiceImpl implements EventService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final EventRepository eventRepository;
  private final EventCategoryRepository eventCategoryRepository;
  private final EventShowingRepository eventShowingRepository;
  private final LocationRepository locationRepository;
  private final SeatingPlanRepository seatingPlanRepository;
  private final SeatingPlanSectorRepository seatingPlanSectorRepository;
  private final SectorPriceEventShowingRepository sectorPriceEventShowingRepository;
  private final BookingRepository bookingRepository;
  private final BookingNonSeatRepository bookingNonSeatRepository;
  private final BookingSeatRepository bookingSeatRepository;
  private final ArtistPerformanceRepository artistPerformanceRepository;
  private final TopEventRepository topEventRepository;
  private final EventMapper eventMapper;
  private final ImageServiceImpl imageService;

  @Override
  public List<TopEvent> findTopTen(Long categoryId) {
    return this.topEventRepository.findTopEvents(categoryId, 10);
  }

  @Override
  public List<Event> findAll() {
    return eventRepository.findAllByOrderByTitleDesc();
  }

  @Transactional
  @Override
  public Event create(EventCreationDto eventCreationDto) {
    try {
      Duration duration =
          Duration.ofHours(eventCreationDto.duration().hours())
              .plusMinutes(eventCreationDto.duration().minutes());
      Event eventToCreate =
          new Event(
              null,
              eventCreationDto.title(),
              eventCreationDto.categoryId(),
              duration,
              eventCreationDto.description(),
              eventCreationDto.imageRef());
      var createdEvent = eventRepository.save(eventToCreate);

      for (Long artistId : eventCreationDto.artistIds()) {
        artistPerformanceRepository.save(new ArtistPerformance(artistId, createdEvent.getId()));
      }
      Long id = createdEvent.getId();
      eventRepository.changeImageRefFromEventWithEventId(id, "e_" + id);

      for (EventCreationDto.Showing showing : eventCreationDto.showings()) {
        EventShowing eventShowing =
            new EventShowing(null, showing.occursOn(), showing.performedAt(), createdEvent.getId());
        Timestamp endTime = new Timestamp(showing.occursOn().getTime() + duration.getSeconds());
        if (eventShowingRepository.isOccupied(showing.performedAt(), showing.occursOn(), endTime)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        eventShowing = eventShowingRepository.save(eventShowing);
        for (EventCreationDto.Pricing pricing : showing.pricings()) {
          sectorPriceEventShowingRepository.save(
              new SectorPriceEventShowing(
                  eventShowing.getId(),
                  showing.performedAt(),
                  pricing.sectorId(),
                  pricing.price()));
        }
      }

      return createdEvent;
    } catch (NullPointerException e) {
      throw new CouldNotCreateEntityException(e.getMessage());
    }
  }

  @Override
  public Optional<EventDto> findById(Long id) {
    LOGGER.info("Find Event with this id:{}", id);
    Event e = eventRepository.findById(id).orElse(null);
    if (e == null) {
      return Optional.empty();
    }
    return Optional.of(
        new EventDto(
            e.getId(),
            e.getTitle(),
            e.getCategoryId(),
            e.getDescription(),
            imageService.imageToBase64(e.getImageRef())));
  }

  @Override
  public Optional<EventCategory> findEventCategoryById(Long id) {
    LOGGER.info("Find EventCategory with this id:{}", id);
    return eventCategoryRepository.findById(id);
  }

  @Override
  public List<EventCategory> findCategories() {
    return eventCategoryRepository.findAllByOrderByDisplayName();
  }

  @Override
  public Optional<Page<EventDetailsShowsDto>> findShowingsById(Long eventId, Pageable pageable) {
    LOGGER.info("Find Event showings with this id:{}", eventId);
    try {
      Event event = eventRepository.findById(eventId).orElse(null);
      List<EventDetailsShowsDto> eventShowingDtos = new ArrayList<>();
      Page<EventShowing> eventShowings =
          eventShowingRepository.findAllByEventId(eventId, pageable).orElse(null);
      for (EventShowing eventShowing : eventShowings) {
        if (eventShowing.getOccursOn().toLocalDateTime().isAfter(LocalDateTime.now())) {
          SeatingPlan seatingPlan =
              seatingPlanRepository.findById(eventShowing.getPerformedAt()).orElse(null);
          Location location = locationRepository.findById(seatingPlan.getLocatedIn()).orElse(null);

          BigDecimal lowestPrice =
              findLowestPriceInShowing(eventShowing.getId(), seatingPlan.getId());
          BigDecimal highestPrice =
              findHighestPriceInShowing(eventShowing.getId(), seatingPlan.getId());
          LocalDateTime localDateTime = eventShowing.getOccursOn().toLocalDateTime();
          DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
          DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          String startTime = localDateTime.toLocalTime().format(dtfTime);
          String endTime =
              localDateTime.toLocalTime().plusNanos(event.getDuration().toNanos()).format(dtfTime);
          boolean bookedOut = lowestPrice == null;
          eventShowingDtos.add(
              new EventDetailsShowsDto(
                  eventShowing.getId(),
                  event.getId(),
                  event.getTitle(),
                  localDateTime.toLocalDate().format(dtfDate),
                  startTime + "-" + endTime,
                  location.getName(),
                  seatingPlan.getName(),
                  lowestPrice,
                  highestPrice,
                  bookedOut));
        }
      }
      final Page<EventDetailsShowsDto> page =
          new PageImpl<>(eventShowingDtos, pageable, eventShowings.getTotalElements());
      return Optional.of(page);
    } catch (NullPointerException e) {
      return Optional.empty();
    }
  }

  @Override
  public Page<EventDetailsShowsDto> filterShowings(
      EventShowingFilterDto eventShowingFilterDto, Pageable pageable) {
    LOGGER.debug("Getting shows matching search criteria");

    List<EventDetailsShowsDto> showingDetailsList = new ArrayList<>();
    Page<EventShowing> showingPage =
        eventShowingRepository.findFiltered(
            eventShowingFilterDto.eventTitle(),
            eventShowingFilterDto.locationName(),
            eventShowingFilterDto.date(),
            eventShowingFilterDto.startTime(),
            eventShowingFilterDto.endTime(),
            eventShowingFilterDto.minPrice(),
            eventShowingFilterDto.maxPrice(),
            pageable);

    if (showingPage.getTotalElements() == 0) {
      return Page.empty();
    }

    for (EventShowing eventShowing : showingPage) {
      Event event = eventRepository.findById(eventShowing.getEventId()).get();

      showingDetailsList.add(getShowingDetails(eventShowing, event));
    }

    final Page<EventDetailsShowsDto> showingDetailsPage =
        new PageImpl<>(showingDetailsList, pageable, showingPage.getTotalElements());

    return showingDetailsPage;
  }

  @Override
  public List<SectorPriceAndName> findPrices(Long showingId) {
    if (!eventShowingRepository.existsById(showingId)) {
      throw new NotFoundException("There is no showing with this id.");
    }

    return sectorPriceEventShowingRepository.findPriceAndNameByShowing(showingId);
  }

  @Override
  public EventShowing findShowingById(Long showingId) {
    if (!eventShowingRepository.existsById(showingId)) {
      throw new NotFoundException("There is no showing with this id.");
    }

    return eventShowingRepository.findById(showingId).get();
  }

  public Boolean isOccupied(Long seatingPlanId, Timestamp start, Timestamp end) {
    return eventShowingRepository.isOccupied(seatingPlanId, start, end);
  }

  @Override
  public EventShowing findShowingByIdAndEventId(Long id, Long eventId) {
    Optional<EventShowing> optionalShowing =
        this.eventShowingRepository.findByIdAndEventId(id, eventId);

    if (!optionalShowing.isPresent()) {
      throw new NotFoundException(
          "Failed to load the page: the requested showing does not exist. Please contact our support team.");
    }

    return optionalShowing.get();
  }

  @Override
  public List<EventResponseDto> findEventByTitle(String title) {
    return eventRepository.findTop3ByTitleContainingAllIgnoreCase(title).stream()
        .map(eventMapper::eventToEventResponseDto)
        .toList();
  }

  @Override
  public List<EventSearchResultDto> filterEvents(
      String nameOrContent,
      Long categoryId,
      Long hours,
      Long minutes,
      Long pageNumber,
      Long pageSize) {

    Duration minDuration = null;
    Duration maxDuration = null;

    if (hours != null && minutes != null) {
      Duration baseDuration = Duration.ofHours(hours).plusMinutes(minutes);
      minDuration = baseDuration.plusMinutes(-30);
      maxDuration = baseDuration.plusMinutes(30);
    }

    List<Event> events =
        eventRepository.filterEvents(
            nameOrContent, categoryId, minDuration, maxDuration, pageNumber, pageSize);

    List<EventSearchResultDto> eventDtos = new ArrayList<>();
    for (Event event : events) {
      List<EventShowing> showings =
          eventShowingRepository
              .findAllByEventId(event.getId(), PageRequest.of(0, Integer.MAX_VALUE))
              .orElse(null)
              .toList();

      eventDtos.add(
          new EventSearchResultDto(
              event.getId(),
              event.getTitle(),
              event.getDescription(),
              event.getCategoryId(),
              eventIsBookedOut(showings),
              eventHasShowingsInFuture(showings),
              imageService.imageToBase64(event.getImageRef()),
              event.getDuration().toHours(),
              event.getDuration().toMinutesPart()));
    }

    return eventDtos;
  }

  private boolean checkSeatsAvailableInSector(
      SeatingPlanSector seatingPlanSector, List<Booking> bookings) {
    int seats = seatingPlanSector.getCapacity();
    if (seatingPlanSector.getType() == SectorType.standing) {
      for (Booking booking : bookings) {
        BookingNonSeat bookingNonSeat =
            bookingNonSeatRepository.findByBookingIdAndSeatingPlanSector(
                booking.getId(), seatingPlanSector.getNumber());
        if (bookingNonSeat != null) {
          seats -= bookingNonSeat.getAmount();
        }
      }
    } else {
      for (Booking booking : bookings) {
        seats -=
            bookingSeatRepository.countByBookingIdAndSeatingPlanSector(
                booking.getId(), seatingPlanSector.getNumber());
      }
    }
    return seats > 0;
  }

  private EventDetailsShowsDto getShowingDetails(EventShowing eventShowing, Event event) {
    SeatingPlan seatingPlan =
        seatingPlanRepository.findById(eventShowing.getPerformedAt()).orElse(null);

    Location location = locationRepository.findById(seatingPlan.getLocatedIn()).orElse(null);

    BigDecimal lowestPrice = findLowestPriceInShowing(eventShowing.getId(), seatingPlan.getId());
    BigDecimal highestPrice = findHighestPriceInShowing(eventShowing.getId(), seatingPlan.getId());
    LocalDateTime occursOn = eventShowing.getOccursOn().toLocalDateTime();
    DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    String startTime = occursOn.toLocalTime().format(dtfTime);
    String endTime =
        occursOn.toLocalTime().plusNanos(event.getDuration().toNanos()).format(dtfTime);
    boolean bookedOut = lowestPrice == null;

    EventDetailsShowsDto showingDetails =
        new EventDetailsShowsDto(
            eventShowing.getId(),
            event.getId(),
            event.getTitle(),
            occursOn.toLocalDate().format(dtfDate),
            startTime + "-" + endTime,
            location.getName(),
            seatingPlan.getName(),
            lowestPrice,
            highestPrice,
            bookedOut);

    return showingDetails;
  }

  private BigDecimal findLowestPriceInShowing(Long showingId, Long seatingPlanId) {
    List<BigDecimal> prices = findAllPricesOfShowing(showingId, seatingPlanId);

    if (prices.isEmpty()) {
      return null;
    }
    return prices.stream().reduce(BigDecimal::min).get();
  }

  private BigDecimal findHighestPriceInShowing(Long showingId, Long seatingPlanId) {
    List<BigDecimal> prices = findAllPricesOfShowing(showingId, seatingPlanId);

    if (prices.isEmpty()) {
      return null;
    }
    return prices.stream().reduce(BigDecimal::max).get();
  }

  private List<BigDecimal> findAllPricesOfShowing(Long showingId, Long seatingPlanId) {
    List<Booking> activeBookings = bookingRepository.findActiveBookingsForShowing(showingId);
    List<SeatingPlanSector> seatingPlanSectors =
        seatingPlanSectorRepository.findAllBySeatingPlanId(seatingPlanId);
    List<BigDecimal> prices = new ArrayList<>();

    for (SeatingPlanSector seatingPlanSector : seatingPlanSectors) {
      if (checkSeatsAvailableInSector(seatingPlanSector, activeBookings)) {
        BigDecimal price =
            sectorPriceEventShowingRepository
                .findByEventShowingIdAndSeatingPlanIdAndSeatingPlanSector(
                    showingId, seatingPlanId, seatingPlanSector.getNumber())
                .getPrice();
        if (price != null) {
          prices.add(price);
        }
      }
    }

    return prices;
  }

  private boolean eventHasShowingsInFuture(List<EventShowing> showings) {
    if (showings == null) {
      return false;
    }

    for (EventShowing showing : showings) {
      Date today = new Date();
      if (showing.getOccursOn().after(today)) {
        return true;
      }
    }
    return false;
  }

  private boolean eventIsBookedOut(List<EventShowing> showings) {
    if (showings == null) {
      return true; // No showing means, that nothing is bookable either
    }

    for (EventShowing showing : showings) {
      if (!showingIsBookedOut(showing.getId(), showing.getPerformedAt())) {
        return false;
      }
    }
    return true;
  }

  private boolean showingIsBookedOut(Long showingId, Long seatingPlanId) {
    Long vacantTickets =
        eventShowingRepository.getVacantTicketsByShowingId(showingId, seatingPlanId);
    if (vacantTickets == null) {
      return false;
    }
    return vacantTickets <= 0;
  }
}
