package at.ac.tuwien.sepm.groupphase.backend.performance.meta;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingShowingDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.DetailedNewsEntryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.SimpleNewsEntryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorType;
import at.ac.tuwien.sepm.groupphase.backend.performance.client.TicketLineClient;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EndpointCaller {
  private final TicketLineClient client;

  public EndpointCaller(final TicketLineClient client) {
    this.client = client;
  }

  // Java Method: getLocationsWithEvents
  // Path: GET /locations-events
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationEventDto>
  public void get_locationsEvents() {
    // TODO: implement
  }

  private String generateAlphabeticPostfix(final int length) {
    final int leftLimit = 'a';
    final int rightLimit = 'z';
    final var random = new Random();

    return random
        .ints(leftLimit, rightLimit + 1)
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  // Java Method: createLocation
  // Path: POST /location
  // Returns: org.springframework.http.ResponseEntity<?>
  public void post_location() {
    final var name = "Location " + generateAlphabeticPostfix(15);
    System.out.println("Generated Location: " + name);

    LocationRequestDto location =
        new LocationRequestDto(name, "Austria", "Vienna", "Reumannplatz", "1010");
    this.client.doPost("/location", location, Object.class);
  }

  // Java Method: getLocations
  // Path: GET /locations
  // Returns:
  // java.util.stream.Stream<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationResponseDto>
  public void get_locations() {
    // TODO: implement
  }

  // Java Method: createSeatingPlan
  // Path: POST /location/{id}/seating-plan
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanDto
  public void post_location_id_seatingPlan() {
    SeatingPlanCreationDto.Sector sector =
        new SeatingPlanCreationDto.Sector("Sector", "#fcba03", 10, SectorType.standing, null);

    SeatingPlanCreationDto seatingPlan =
        new SeatingPlanCreationDto(
            "Seating Plan " + generateAlphabeticPostfix(10), 1L, 10, List.of(sector));

    this.client.doPost("/location/1/seating-plan", seatingPlan, SeatingPlanDto.class);
  }

  // Java Method: getOneSeatingPlan
  // Path: GET /location/seating-plan/{seatingPlanId}
  // Returns: class
  // at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanResponseDto
  public void get_location_seatingPlan_seatingPlanId() {
    // TODO: implement
  }

  // Java Method: getSeatingPlans
  // Path: GET /location/{id}/seating-plans
  // Returns: java.util.List<at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan>
  public void get_location_id_seatingPlans() {
    // TODO: implement
  }

  // Java Method: create
  // Path: POST /artist
  // Returns: org.springframework.http.ResponseEntity<?>
  public void post_artist() {
    ArtistRequestDto artist = new ArtistRequestDto("first", "last", "artist");

    this.client.doPost("/artist", artist, ArtistResponseDto.class);
  }

  // Java Method: getArtists
  // Path: GET /artists
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistResponseDto>
  public void get_artists() {
    this.client.doGet("/artists", List.<ArtistResponseDto>of().getClass());
  }

  // Java Method: getLocationsWithEvents
  // Path: GET /artists-events
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistEventDto>
  public void get_artistsEvents() {
    // TODO: implement
  }

  // Java Method: create
  // Path: POST /news-entry
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.DetailedNewsEntryDto
  public void post_newsEntry() {
    NewsEntryCreationDto news =
        new NewsEntryCreationDto("news", "contents", "summary", 1L, "image.jpg", 1L);

    this.client.doPost("/news-entry", news, DetailedNewsEntryDto.class);
  }

  // Java Method: handleFileUpload
  // Path: POST /news-entry/{id}/image
  // Returns: void
  public void post_newsEntry_id_image() {
    // TODO: implement
  }

  // Java Method: getNewsEntryDetails
  // Path: GET /news-entry/{id}
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryDetailsDto
  public void get_newsEntry_id() {
    this.client.doGet("/news-entry/" + 1, NewsEntryDetailsDto.class);
  }

  // Java Method: getNews
  // Path: GET /news
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.SimpleNewsEntryDto>
  public void get_news() {
    this.client.doGet("/news", List.<SimpleNewsEntryDto>of().getClass());
  }

  // Java Method: getAll
  // Path: GET /tickets
  // Returns: void
  public void get_tickets() {
    // Not implemented
  }

  // Java Method: getOne
  // Path: GET /ticket/{id}
  // Returns: void
  public void get_ticket_id() {
    // Not implemented
  }

  // Java Method: update
  // Path: PUT /event/{id}
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.entity.Event
  public void put_event_id() {
    // Not implemented
  }

  // Java Method: create
  // Path: POST /event
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventResponseDto
  public void post_event() {
    EventCreationDto.Pricing pricing = new EventCreationDto.Pricing(1L, BigDecimal.valueOf(10L));

    long minDay = LocalDate.of(1900, 1, 1).toEpochDay();
    long maxDay = LocalDate.now().toEpochDay();
    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
    LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
    LocalTime time = LocalTime.of(0, 0, 0);
    LocalDateTime randomDateTime = LocalDateTime.of(randomDate, time);

    EventCreationDto.Showing showing =
        new EventCreationDto.Showing(Timestamp.valueOf(randomDateTime), 1L, List.of(pricing));

    EventCreationDto event =
        new EventCreationDto(
            "event " + this.generateAlphabeticPostfix(10),
            1L,
            new EventCreationDto.Duration(0L, 1L),
            "description",
            List.of(1L),
            List.of(showing),
            "image.jpg");

    // TODO: why does this cause 400?
    this.client.doPost("/event", event, EventResponseDto.class);
  }

  // Java Method: findAll
  // Path: GET /events
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventResponseDto>
  public void get_events() {
    this.client.doGet("/events", List.<EventResponseDto>of().getClass());
  }

  // Java Method: findCategories
  // Path: GET /event/categories
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCategoryDto>
  public void get_event_categories() {
    this.client.doGet("/event/categories", List.<EventCategoryDto>of().getClass());
  }

  // Java Method: findShowingByIdAndEventId
  // Path: GET /event/{eventId}/showing/{id}
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.ShowingDto
  public void get_event_eventId_showing_id() {
    // TODO: implement
  }

  // Java Method: findById
  // Path: GET /event/{id}
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto
  public void get_event_id() {
    // TODO: implement
  }

  // Java Method: findEventCategoryById
  // Path: GET /event/{id}/category
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory
  public void get_event_id_category() {
    // TODO: implement
  }

  // Java Method: findShowingsById
  // Path: GET /event/{id}/showings
  // Returns:
  // org.springframework.data.domain.Page<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailsShowsDto>
  public void get_event_id_showings() {
    // TODO: implement
  }

  // Java Method: filterShowings
  // Path: GET /showings
  // Returns:
  // org.springframework.data.domain.Page<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailsShowsDto>
  public void get_showings() {
    this.client.doGet("/showings", Object.class);
  }

  // Java Method: findShowingDetailsById
  // Path: GET /showing/{id}/details
  // Returns: class
  // at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingShowingDetailsDto
  public void get_showing_id_details() {
    this.client.doGet("/showing/" + 1 + "/details", BookingShowingDetailsDto.class);
  }

  // Java Method: reserve
  // Path: POST /showing/{id}/reservation
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingDto
  public void post_showing_id_reservation() {
    ReservationDto.NonSeat nonSeat = new ReservationDto.NonSeat(1L, 1);

    ReservationDto reservation =
        new ReservationDto(1L, 26L, BigDecimal.valueOf(10L), null, List.of(nonSeat));

    this.client.doPost("/showing/" + 201 + "/reservation", reservation, BookingDto.class);
  }

  // Java Method: purchase
  // Path: POST /showing/{id}/purchase
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingDto
  public void post_showing_id_purchase() {
    ReservationDto.NonSeat nonSeat = new ReservationDto.NonSeat(1L, 1);

    ReservationDto reservation =
        new ReservationDto(
            1L, // TODO: make sure User ID exists
            26L,
            BigDecimal.valueOf(10L),
            null,
            List.of(nonSeat));

    PurchaseDto purchase = new PurchaseDto(reservation, null, null, null, null);

    this.client.doPost("/showing/" + 201 + "/purchase", purchase, BookingDto.class);
  }

  // Java Method: getReservationInfo
  // Path: GET /booking/{id}/reservation-info
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.ReservationInfoDto
  public void get_booking_id_reservationInfo() {
    // TODO: implement
  }

  // Java Method: reservationToPurchase
  // Path: POST /booking/{id}/reservation-to-purchase
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingDto
  public void post_booking_id_reservationToPurchase() {
    // TODO: implement
  }

  // Java Method: filterEvents
  // Path: GET /events/filter
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventSearchResultDto>
  public void get_events_filter() {
    this.client.doGet("/events/filter", List.<EventSearchResultDto>of().getClass());
  }

  // Java Method: findEvents
  // Path: GET /events-search
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventResponseDto>
  public void get_eventsSearch() {
    this.client.doGet("/events-search", List.<EventSearchResultDto>of().getClass());
  }

  // Java Method: topTen
  // Path: GET /events/top-ten
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TopEventResponseDto>
  public void get_events_topTen() {
    this.client.doGet("/events/top-ten", List.<TopEventResponseDto>of().getClass());
  }

  // Java Method: findShowing
  // Path: GET /showing/{id}
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.ShowingDto
  public void get_showing_id() {
    this.client.doGet("/showing/1", ShowingDto.class);
  }

  // Java Method: handleFileUpload
  // Path: POST /event/{id}/image
  // Returns: void
  public void post_event_id_image() {
    // TODO: missing image
    // this.client.doGet("/event/1/image", String.class);
  }

  // Java Method: findSectorPrices
  // Path: GET /showing/{id}/prices
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventShowingSectorPriceDto>
  public void get_showing_id_prices() {
    this.client.doGet("/showing/1/prices", List.<EventShowingSectorPriceDto>of().getClass());
  }

  // Java Method: findBookedSeatsAndNonSeats
  // Path: GET /showing/{id}/booked-seats
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventShowingBookedSeatsDto>
  public void get_showing_id_bookedSeats() {
    this.client.doGet("/showing/1/booked-seats", List.<EventShowingBookedSeatsDto>of().getClass());
  }

  // Java Method: findBookedNonSeatsAndNonSeats
  // Path: GET /showing/{id}/booked-non-seats
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventShowingBookedNonSeatsDto>
  public void get_showing_id_bookedNonSeats() {
    this.client.doGet(
        "/showing/1/booked-non-seats", List.<EventShowingBookedNonSeatsDto>of().getClass());
  }

  // Java Method: findIfSeatingPlanIsOccupied
  // Path: GET /showing/{seatingPlanId}/occupation
  // Returns: class java.lang.Boolean
  public void get_showing_seatingPlanId_occupation() {
    // TODO: returns 400
    // this.client.doGet("/showing/1/occupation", Boolean.class);
  }

  // Java Method: triggerPasswordReset
  // Path: POST /user/{id}/password-reset
  // Returns: void
  public void post_user_id_passwordReset() {
    // TODO: implement
  }

  // Java Method: findAllBookingsByBookedBy
  // Path: GET /user/{id}/bookings
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.BookingOverviewDto>
  public void get_user_id_bookings() {
    // TODO: implement
  }

  // Java Method: paymentInfoExists
  // Path: GET user/{id}/payment
  // Returns: void
  public void get_user_id_payment() {
    this.client.doGet("/user/" + 1L + "/payment", void.class);
  }

  // Java Method: deleteUser
  // Path: DELETE user/{id}
  // Returns: void
  public void delete_user_id() {
    // TODO: create user and delete created user
  }

  // Java Method: findAllCustomersByName
  // Path: GET /users/admin
  // Returns:
  // org.springframework.data.domain.Page<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.ApplicationUserDto>
  public void get_users_admin() {
    // TODO: implement
  }

  // Java Method: findAllAdminsByName
  // Path: GET /users/super-admin
  // Returns:
  // org.springframework.data.domain.Page<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.ApplicationUserDto>
  public void get_users_superAdmin() {
    // TODO: implement
  }

  // Java Method: findUserByEmail
  // Path: GET /user
  // Returns: class at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.EditUserResponseDto
  public void get_user() {
    // TODO: implement
  }

  // Java Method: triggerPasswordResetByMail
  // Path: POST /users/password-reset
  // Returns: void
  public void post_users_passwordReset() {
    // TODO: implement
  }

  // Java Method: getAll
  // Path: GET /tickets/{invoiceId}
  // Returns: java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketDto>
  public void get_tickets_invoiceId() {
    // TODO: implement
  }

  // Java Method: getInvoice
  // Path: GET /invoice/{id}
  // Returns:
  // java.util.Optional<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.InvoiceDto>
  public void get_invoice_id() {
    // TODO: implement
  }

  // Java Method: getInvoicesByBooking
  // Path: GET /booking/{bookingId}/invoices
  // Returns:
  // java.util.List<at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.SimpleInvoiceDto>
  public void get_booking_bookingId_invoices() {
    // TODO: implement
  }

  public List<Runnable> asRunnables() {
    return List.of(
        () -> this.get_locationsEvents(),
        () -> this.post_location(),
        () -> this.get_locations(),
        () -> this.post_location_id_seatingPlan(),
        () -> this.get_location_seatingPlan_seatingPlanId(),
        () -> this.get_location_id_seatingPlans(),
        () -> this.post_artist(),
        () -> this.get_artists(),
        () -> this.get_artistsEvents(),
        () -> this.post_newsEntry(),
        () -> this.post_newsEntry_id_image(),
        () -> this.get_newsEntry_id(),
        () -> this.get_news(),
        () -> this.get_tickets(),
        () -> this.get_ticket_id(),
        () -> this.put_event_id(),
        () -> this.post_event(),
        () -> this.get_events(),
        () -> this.get_event_categories(),
        () -> this.get_event_eventId_showing_id(),
        () -> this.get_event_id(),
        () -> this.get_event_id_category(),
        () -> this.get_event_id_showings(),
        () -> this.get_showings(),
        () -> this.get_showing_id_details(),
        () -> this.post_showing_id_reservation(),
        () -> this.post_showing_id_purchase(),
        () -> this.get_booking_id_reservationInfo(),
        () -> this.post_booking_id_reservationToPurchase(),
        () -> this.get_events_filter(),
        () -> this.get_eventsSearch(),
        () -> this.get_events_topTen(),
        () -> this.get_showing_id(),
        () -> this.post_event_id_image(),
        () -> this.get_showing_id_prices(),
        () -> this.get_showing_id_bookedSeats(),
        () -> this.get_showing_id_bookedNonSeats(),
        () -> this.get_showing_seatingPlanId_occupation(),
        () -> this.post_user_id_passwordReset(),
        () -> this.get_user_id_bookings(),
        () -> this.get_user_id_payment(),
        () -> this.delete_user_id(),
        () -> this.get_users_admin(),
        () -> this.get_users_superAdmin(),
        () -> this.get_user(),
        () -> this.post_users_passwordReset(),
        () -> this.get_tickets_invoiceId(),
        () -> this.get_invoice_id(),
        () -> this.get_booking_bookingId_invoices());
  }
}
