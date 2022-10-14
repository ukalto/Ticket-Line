package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.BookingMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.event.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotCreateEntityException;
import at.ac.tuwien.sepm.groupphase.backend.service.booking.BookingService;
import at.ac.tuwien.sepm.groupphase.backend.service.event.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class EventEndpoint {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final EventService eventService;
  private final EventMapper eventMapper;
  private final ImageService imageService;
  private final BookingService bookingService;
  private final BookingMapper bookingMapper;

  @Secured({"ROLE_ADMINISTRATOR", "ROLE_USER"})
  @GetMapping("/events")
  @Operation(
      summary = "Lists events matching the search criteria",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventResponseDto> findAll() {
    LOGGER.info("GET /api/v1/events");
    return eventService.findAll().stream()
        .map(eventMapper::eventToEventResponseDto)
        .collect(Collectors.toList());
  }

  @PermitAll
  @GetMapping("/events-search")
  @Operation(
      summary = "Lists events matching the title",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventResponseDto> findEvents(
      @RequestParam(required = false, defaultValue = "") String title) {
    LOGGER.info("GET /api/v1/events-search with title: {}", title);
    return eventService.findEventByTitle(title);
  }

  @PermitAll()
  @GetMapping("/events/top-ten")
  @Operation(
      summary = "List top 10 events by category.",
      security = @SecurityRequirement(name = "apiKey"))
  public List<TopEventResponseDto> topTen(@RequestParam Optional<Long> categoryId) {
    LOGGER.info("GET /api/v1/events/top-ten");
    var categoryIdParam = categoryId.isPresent() ? categoryId.get() : null;
    return this.eventService.findTopTen(categoryIdParam).stream()
        .map(this.eventMapper::topEventToTopEventResponseDto)
        .collect(Collectors.toList());
  }

  @GetMapping("/event/categories")
  @Operation(
      summary = "Get all available categories.",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventCategoryDto> findCategories() {
    LOGGER.info("GET /api/v1/event/categories");
    return eventService.findCategories().stream()
        .map(eventMapper::eventCategoryToEventCategoryDto)
        .sorted(Comparator.comparing(EventCategoryDto::displayName))
        .collect(Collectors.toList());
  }

  @Secured("ROLE_ADMINISTRATOR")
  @PostMapping("/event")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a new event with an arbitrary number of showings",
      security = @SecurityRequirement(name = "apiKey"))
  public EventResponseDto create(@Valid @RequestBody EventCreationDto event) {
    try {
      LOGGER.info("POST /api/v1/event body: {}", event);
      return eventMapper.eventToEventResponseDto(eventService.create(event));
    } catch (CouldNotCreateEntityException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @PermitAll()
  @GetMapping("/showing/{id}")
  @Operation(summary = "Returns the showing with the given id")
  public ShowingDto findShowing(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/showing/{id}");

    return eventMapper.showingEntityToDto(eventService.findShowingById(id));
  }

  @GetMapping("/event/{eventId}/showing/{id}")
  @Operation(
      summary = "Lists showings matching the search criteria",
      security = @SecurityRequirement(name = "apiKey"))
  public ShowingDto findShowingByIdAndEventId(@PathVariable Long id, @PathVariable Long eventId) {
    LOGGER.info("Finding EventShowing with id:{} and event id: {}", id, eventId);

    return eventMapper.showingEntityToDto(eventService.findShowingByIdAndEventId(id, eventId));
  }

  @PermitAll()
  @GetMapping("/event/{id}")
  @Operation(
      summary = "List one event by ID. Includes a set of references to all related showings.",
      security = @SecurityRequirement(name = "apiKey"))
  public EventDto findById(@PathVariable Long id) throws IOException {
    LOGGER.info("Finding event with id:{}", id);
    return this.eventService.findById(id).orElse(null);
  }

  @PermitAll()
  @GetMapping("/event/{id}/category")
  @Operation(
      summary = "List one event by ID. Includes a set of references to all related showings.",
      security = @SecurityRequirement(name = "apiKey"))
  public EventCategory findEventCategoryById(@PathVariable Long id) {
    LOGGER.info("Finding EventCategory with id:{}", id);
    return this.eventService.findEventCategoryById(id).orElse(null);
  }

  @PermitAll()
  @GetMapping("/event/{id}/showings")
  @Operation(
      summary = "Lists showings matching given event id",
      security = @SecurityRequirement(name = "apiKey"))
  public Page<EventDetailsShowsDto> findShowingsById(
      @PathVariable Long id, @RequestParam int page, @RequestParam int size) {
    LOGGER.info("Finding EventShowings with id:{}", id);
    return this.eventService.findShowingsById(id, PageRequest.of(page, size)).orElse(null);
  }

  @GetMapping("/showings")
  @Operation(
      summary = "Lists showings matching the search criteria ",
      security = @SecurityRequirement(name = "apiKey"))
  public Page<EventDetailsShowsDto> filterShowings(
      @RequestParam(required = false) String eventTitle,
      @RequestParam(required = false) String locationName,
      @RequestParam(required = false) String date,
      @RequestParam(required = false) LocalTime startTime,
      @RequestParam(required = false) LocalTime endTime,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam int page,
      @RequestParam int size) {
    LOGGER.info("Filtering showings based on given search criteria");
    LocalDate parsedDate;
    try {
      parsedDate = date == null ? null : LocalDate.parse(date);
    } catch (DateTimeParseException e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Given date has an incorrect format");
    }

    EventShowingFilterDto filterDto =
        new EventShowingFilterDto(
            eventTitle, locationName, parsedDate, startTime, endTime, minPrice, maxPrice);

    return eventService.filterShowings(filterDto, PageRequest.of(page, size));
  }

  @GetMapping("/showing/{id}/details")
  public BookingShowingDetailsDto findShowingDetailsById(@PathVariable Long id) {
    LOGGER.info("Finding showing with id:{}", id);
    return this.bookingService.findShowingDetailsById(id);
  }

  @Secured("ROLE_ADMINISTRATOR")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/event/{id}/image")
  @Operation(summary = "Saves an image to the related event")
  public void handleFileUpload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    LOGGER.info("POST /api/v1/event/{}/image", id);
    imageService.store(file, "e_" + id);
  }

  @Secured("ROLE_USER")
  @PostMapping("/showing/{id}/reservation")
  @Operation(summary = "Creates a new booking", security = @SecurityRequirement(name = "apiKey"))
  public BookingDto reserve(
      @Valid @RequestBody ReservationDto reservationDto, @PathVariable Long id) {
    LOGGER.info("Making reservation");

    return bookingMapper.bookingEntityToBookingDto(bookingService.reserve(reservationDto, id));
  }

  @Secured("ROLE_USER")
  @PostMapping("/showing/{id}/purchase")
  @Operation(
      summary = "Creates a new booking and invoice of type purchase",
      security = @SecurityRequirement(name = "apiKey"))
  public BookingDto purchase(@Valid @RequestBody PurchaseDto purchaseDto, @PathVariable Long id) {
    LOGGER.info("Making purchase");
    return bookingMapper.bookingEntityToBookingDto(bookingService.purchase(purchaseDto, id));
  }

  @Secured("ROLE_USER")
  @GetMapping("/booking/{id}/reservation-info")
  @Operation(
      summary = "Returns valuable information about an existing reservation",
      security = @SecurityRequirement(name = "apiKey"))
  public ReservationInfoDto getReservationInfo(@PathVariable Long id) {
    LOGGER.info("Getting reservation information");

    return bookingService.getReservationInfo(id);
  }

  @Secured("ROLE_USER")
  @PostMapping("/booking/{id}/reservation-to-purchase")
  @Operation(
      summary = "Turns a prior reservation into a purchase by creating an invoice of type purchase",
      security = @SecurityRequirement(name = "apiKey"))
  public BookingDto reservationToPurchase(
      @PathVariable Long id, @Valid @RequestBody ReservationPurchaseDto reservationPurchaseDto) {
    LOGGER.info("Turning reservation into purchase");

    return bookingMapper.bookingEntityToBookingDto(
        bookingService.reservationToPurchase(id, reservationPurchaseDto));
  }

  @Secured("ROLE_USER")
  @GetMapping("/showing/{id}/prices")
  @Operation(
      summary = "Lists prices of all sectors in the seating plan of the showing",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventShowingSectorPriceDto> findSectorPrices(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/showing/{id}/prices");

    return eventService.findPrices(id).stream()
        .map(eventMapper::showingSectorPriceEntityToDto)
        .collect(Collectors.toList());
  }

  @Secured("ROLE_USER")
  @GetMapping("/showing/{id}/booked-seats")
  @Operation(
      summary = "Lists all seats that are already booked for the showing",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventShowingBookedSeatsDto> findBookedSeatsAndNonSeats(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/showing/{id}/booked-seats");

    return bookingService.findBookedSeats(id).stream()
        .map(eventMapper::showingBookedSeatsEntityToDto)
        .collect(Collectors.toList());
  }

  @Secured("ROLE_USER")
  @GetMapping("/showing/{id}/booked-non-seats")
  @Operation(
      summary = "Lists all non-seats that are already booked for the showing",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventShowingBookedNonSeatsDto> findBookedNonSeatsAndNonSeats(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/showing/{id}/booked-non-seats");

    return bookingService.findBookedNonSeats(id).stream()
        .map(eventMapper::showingBookedNonSeatsEntityToDto)
        .collect(Collectors.toList());
  }

  @Secured("ROLE_ADMINISTRATOR")
  @GetMapping("/showing/{seatingPlanId}/occupation")
  @Operation(
      summary = "Returns true if the seating plan is occupied at the given time",
      security = @SecurityRequirement(name = "apiKey"))
  public Boolean findIfSeatingPlanIsOccupied(
      @PathVariable Long seatingPlanId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") Date start,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") Date end) {
    LOGGER.info("GET /api/v1/showing/{}/occupation?start={}&end={}", seatingPlanId, start, end);
    return eventService.isOccupied(
        seatingPlanId, new Timestamp(start.getTime()), new Timestamp(end.getTime()));
  }

  @GetMapping("/events/filter")
  @Operation(
      summary = "Lists events, that match the search criteria",
      security = @SecurityRequirement(name = "apiKey"))
  public List<EventSearchResultDto> filterEvents(
      @RequestParam(required = false) String nameOrContent,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long hours,
      @RequestParam(required = false) Long minutes,
      @RequestParam(required = false, defaultValue = "0") Long pageNumber,
      @RequestParam(required = false, defaultValue = "10") Long pageSize) {
    LOGGER.info("GET /events/filter");
    return eventService.filterEvents(
        nameOrContent, categoryId, hours, minutes, pageNumber, pageSize);
  }
}
