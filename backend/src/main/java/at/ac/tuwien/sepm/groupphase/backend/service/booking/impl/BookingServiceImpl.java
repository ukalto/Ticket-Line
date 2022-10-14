package at.ac.tuwien.sepm.groupphase.backend.service.booking.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.BookingOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.MissingPaymentInformationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedNonSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingNonSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventShowingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.booking.BookingService;
import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BookingServiceImpl implements BookingService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final BookingRepository bookingRepository;
  private final EventShowingRepository eventShowingRepository;
  private final EventRepository eventRepository;
  private final InvoiceRepository invoiceRepository;
  private final ImageService imageService;
  private final BookingSeatRepository bookingSeatRepository;
  private final BookingNonSeatRepository bookingNonSeatRepository;
  private final ApplicationUserRepository applicationUserRepository;
  private final SeatingPlanRepository seatingPlanRepository;
  private final SeatingPlanSectorRepository seatingPlanSectorRepository;
  private final SeatingPlanSeatRepository seatingPlanSeatRepository;
  private final SeatingPlanRowRepository seatingPlanRowRepository;
  private final UserService userService;
  private final LocationRepository locationRepository;

  @Override
  public List<BookingOverviewDto> findAllBookingsByBookedBy(Long bookedBy) {
    List<Booking> bookings = bookingRepository.findAllByBookedBy(bookedBy);
    List<BookingOverviewDto> bookingOverviewDtos = new ArrayList<>();
    for (Booking booking : bookings) {
      final var amountTickets =
          bookingSeatRepository.countByBookingId(booking.getId())
              + bookingNonSeatRepository.findAllByBookingId(booking.getId()).stream()
                  .map(BookingNonSeat::getAmount)
                  .mapToInt(Integer::intValue)
                  .sum();

      final var eventShowing =
          eventShowingRepository.findById(booking.getEventShowingId()).orElse(null);

      final var event = eventRepository.findById(eventShowing.getEventId()).orElse(null);
      final var localDateTime = eventShowing.getOccursOn().toLocalDateTime();
      final var dtfTime = DateTimeFormatter.ofPattern("HH:mm");
      final var dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

      final String date = localDateTime.toLocalDate().format(dtfDate);
      final String time =
          localDateTime.toLocalTime().format(dtfTime)
              + "-"
              + localDateTime
                  .toLocalTime()
                  .plus(event.getDuration().toHours(), ChronoUnit.HOURS)
                  .format(dtfTime);

      final String title = event.getTitle();
      final String base64 = imageService.imageToBase64(event.getImageRef());

      final var invoices = invoiceRepository.findAllByBookingId(booking.getId());

      Long invoiceNumber = null;
      Long cancellationInvoiceNumber = null;
      BookingState bookingState;

      final var hasInvoices = !invoices.isEmpty();
      if (hasInvoices) {
        var maybePurchaseInvoice =
            invoices.stream()
                .filter(invoice -> invoice.getInvoiceType().equals(InvoiceType.purchase))
                .findFirst();

        if (maybePurchaseInvoice.isPresent()) {
          invoiceNumber = maybePurchaseInvoice.get().getInvoiceNumber();
        }

        var maybeCancellationInvoice =
            invoices.stream()
                .filter(invoice -> invoice.getInvoiceType().equals(InvoiceType.cancellation))
                .findFirst();

        if (maybeCancellationInvoice.isPresent()) {
          cancellationInvoiceNumber = maybeCancellationInvoice.get().getInvoiceNumber();
        }
      }

      if (booking.isCancelled()) {
        bookingState = BookingState.Cancelled;
      } else {
        bookingState = hasInvoices ? BookingState.Paid : BookingState.Reserved;
      }

      bookingOverviewDtos.add(
          new BookingOverviewDto(
              booking.getId(),
              invoiceNumber,
              cancellationInvoiceNumber,
              base64,
              title,
              date,
              time,
              amountTickets,
              booking.getCost(),
              bookingState));
    }

    return bookingOverviewDtos;
  }

  @Override
  public BookingShowingDetailsDto findShowingDetailsById(Long id) {
    LOGGER.info("Find Event showing details with this id:{}", id);
    try {
      EventShowing showing = eventShowingRepository.findById(id).orElse(null);
      Event event = eventRepository.findById(showing.getEventId()).orElse(null);
      SeatingPlan seatingPlan =
          seatingPlanRepository.findById(showing.getPerformedAt()).orElse(null);
      Location location = locationRepository.findById(seatingPlan.getLocatedIn()).orElse(null);

      LocalDateTime showingOccursOn = showing.getOccursOn().toLocalDateTime();
      String showingDate = toDateString(showingOccursOn);
      String showingDuration = toTimeRangeString(showingOccursOn, event.getDuration());

      return new BookingShowingDetailsDto(
          showingDate, showingDuration, location.getName(), seatingPlan.getName());
    } catch (NullPointerException e) {
      LOGGER.error(e.getMessage());
      throw new NotFoundException(
          "Could not load showing details. Missing at least one of the needed entities.");
    }
  }

  private String toDateString(LocalDateTime date) {
    DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return date.toLocalDate().format(dtfDate);
  }

  private String toTimeRangeString(LocalDateTime startDateTime, Duration duration) {
    DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
    LocalTime startTime = startDateTime.toLocalTime();
    LocalTime endTime = startTime.plus(duration.toHours(), ChronoUnit.HOURS);

    return startTime.format(dtfTime) + "-" + endTime.format(dtfTime);
  }

  @Transactional
  @Override
  public Booking reserve(ReservationDto reservationDto, Long eventShowingId) {
    LOGGER.debug("Creating booking");

    checkPresenceOfAllReservationEntities(reservationDto, eventShowingId);
    checkSeatAndNonSeatVacancies(reservationDto, eventShowingId);

    return bookSeatsAndNonSeats(reservationDto, eventShowingId);
  }

  private Booking bookSeatsAndNonSeats(ReservationDto reservationDto, Long eventShowingId) {
    Booking booking = new Booking();
    booking.setBookedAt(Timestamp.from(Instant.now()));
    booking.setBookedBy(reservationDto.bookedBy());
    booking.setCancelled(false);
    booking.setEventShowingId(eventShowingId);
    booking.setCost(reservationDto.cost());

    Booking savedBooking = bookingRepository.save(booking);

    for (ReservationDto.Seat seat : reservationDto.bookedSeats()) {
      BookingSeat bookingSeat =
          new BookingSeat(
              savedBooking.getId(),
              seat.seat(),
              seat.row(),
              reservationDto.seatingPlanId(),
              seat.sector());
      bookingSeatRepository.save(bookingSeat);
    }

    for (ReservationDto.NonSeat nonSeat : reservationDto.bookedNonSeats()) {
      BookingNonSeat bookingNonSeat =
          new BookingNonSeat(
              savedBooking.getId(),
              nonSeat.sector(),
              reservationDto.seatingPlanId(),
              nonSeat.amount());
      bookingNonSeatRepository.save(bookingNonSeat);
    }

    return savedBooking;
  }

  private void checkSeatAndNonSeatVacancies(ReservationDto reservationDto, Long eventShowingId) {
    List<ReservationDto.Seat> doubleSeatBookings = new ArrayList<>();
    for (ReservationDto.Seat seat : reservationDto.bookedSeats()) {
      Optional<BookedSeat> bookedSeat =
          bookingSeatRepository.findBookedSeatByShowingAndByPosition(
              eventShowingId, seat.sector(), seat.row(), seat.seat());

      if (bookedSeat.isPresent()) {
        doubleSeatBookings.add(seat);
      }
    }

    int doubleNonSeatBookingsAmount = 0;
    for (ReservationDto.NonSeat nonSeat : reservationDto.bookedNonSeats()) {
      Long bookedNonSeatAmount =
          bookingNonSeatRepository.findBookedNonSeatsByShowingIdAndSectorId(
              eventShowingId, nonSeat.sector());
      SeatingPlanSector sector =
          seatingPlanSectorRepository.findBySeatingPlanIdAndNumber(
              reservationDto.seatingPlanId(), nonSeat.sector());
      Long vacantSeats = sector.getCapacity() - bookedNonSeatAmount;

      if (nonSeat.amount() > vacantSeats) {
        doubleNonSeatBookingsAmount += nonSeat.amount() - vacantSeats;
      }
    }

    if (!doubleSeatBookings.isEmpty() || doubleNonSeatBookingsAmount > 0) {
      String seatExplanation = "";
      if (!doubleSeatBookings.isEmpty()) {
        seatExplanation = doubleSeatBookings.size() + " of your seats";
      }

      String nonSeatExplanation = "";
      if (doubleNonSeatBookingsAmount > 0) {
        nonSeatExplanation = doubleNonSeatBookingsAmount + " of your standing places";
      }

      String combination = "";
      if (!doubleSeatBookings.isEmpty() && doubleNonSeatBookingsAmount > 0) {
        combination = " and ";
      }

      throw new AlreadyExistsException(
          "Couldn't conclude booking:"
              + " Someone else already booked "
              + seatExplanation
              + combination
              + nonSeatExplanation
              + "."
              + " Please choose other places.");
    }
  }

  private void checkPresenceOfAllReservationEntities(
      ReservationDto reservationDto, Long eventShowingId) {
    checkPresence(applicationUserRepository, reservationDto.bookedBy(), "user");
    checkPresence(eventShowingRepository, eventShowingId, "showing");
    checkPresence(seatingPlanRepository, reservationDto.seatingPlanId(), "seating plan");

    for (ReservationDto.Seat seat : reservationDto.bookedSeats()) {
      SeatingPlanSector.PrimaryKeys sectorKeys = new SeatingPlanSector.PrimaryKeys();
      sectorKeys.setNumber(seat.sector());
      sectorKeys.setSeatingPlan(reservationDto.seatingPlanId());
      checkPresence(seatingPlanSectorRepository, sectorKeys, "sector");

      SeatingPlanRow.PrimaryKeys rowKeys = new SeatingPlanRow.PrimaryKeys();
      rowKeys.setNumber(seat.row());
      rowKeys.setSeatingPlanSector(seat.sector());
      rowKeys.setSeatingPlanId(reservationDto.seatingPlanId());
      checkPresence(seatingPlanRowRepository, rowKeys, "row");

      SeatingPlanSeat.PrimaryKeys seatKeys = new SeatingPlanSeat.PrimaryKeys();
      seatKeys.setNumber(seat.seat());
      seatKeys.setSeatingPlanRow(seat.row());
      seatKeys.setSeatingPlanSector(seat.sector());
      seatKeys.setSeatingPlanId(reservationDto.seatingPlanId());
      checkPresence(seatingPlanSeatRepository, seatKeys, "seat");
    }

    for (ReservationDto.NonSeat nonSeat : reservationDto.bookedNonSeats()) {
      SeatingPlanSector.PrimaryKeys sectorKeys = new SeatingPlanSector.PrimaryKeys();
      sectorKeys.setNumber(nonSeat.sector());
      sectorKeys.setSeatingPlan(reservationDto.seatingPlanId());
      checkPresence(seatingPlanSectorRepository, sectorKeys, "sector");
    }
  }

  @Transactional
  @Override
  public Booking purchase(PurchaseDto purchaseDto, Long eventShowingId) {
    Booking savedBooking = reserve(purchaseDto.bookingInfo(), eventShowingId);
    LOGGER.debug("Creating invoice of type purchase");

    if (invoiceRepository.existsByBookingIdAndInvoiceType(
        savedBooking.getId(), InvoiceType.purchase)) {
      throw new AlreadyExistsException(
          "A purchase with booking id " + savedBooking.getId() + " was already made");
    }

    PaymentInfoDto paymentInfoDto =
        new PaymentInfoDto(
            purchaseDto.cardOwner(),
            purchaseDto.cardNumber(),
            purchaseDto.cardExpirationDate(),
            purchaseDto.cardCvv());

    checkPaymentInfo(paymentInfoDto, savedBooking.getBookedBy());

    Invoice invoice = new Invoice();
    invoice.setBookingId(savedBooking.getId());
    invoice.setPurchasedAt(Timestamp.from(Instant.now()));
    invoice.setInvoiceType(InvoiceType.purchase);

    invoiceRepository.save(invoice);

    return savedBooking;
  }

  @Transactional
  @Override
  public Booking reservationToPurchase(
      Long bookingId, ReservationPurchaseDto reservationPurchaseDto) {
    LOGGER.debug("Creating invoice of type purchase for an existing booking");

    Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
    if (!optionalBooking.isPresent()) {
      throw new NotFoundException("Referenced booking with id " + bookingId + " does not exist!");
    }

    List<BookingSeat> currentSeats = bookingSeatRepository.findAllByBookingId(bookingId);
    List<BookingSeat> newSeats = new ArrayList<>();

    for (ReservationPurchaseDto.Seat seat : reservationPurchaseDto.bookedSeats()) {
      newSeats.add(
          new BookingSeat(
              bookingId,
              seat.seat(),
              seat.row(),
              reservationPurchaseDto.seatingPlanId(),
              seat.sector()));
    }

    if (!newSeats.equals(currentSeats)) {
      this.bookingSeatRepository.deleteAllByBookingId(bookingId);
      this.bookingSeatRepository.saveAll(newSeats);
    }

    List<BookingNonSeat> currentNonSeats = bookingNonSeatRepository.findAllByBookingId(bookingId);
    List<BookingNonSeat> newNonSeats = new ArrayList<>();

    for (ReservationPurchaseDto.NonSeat nonSeat : reservationPurchaseDto.bookedNonSeats()) {
      newNonSeats.add(
          new BookingNonSeat(
              bookingId,
              nonSeat.sector(),
              reservationPurchaseDto.seatingPlanId(),
              nonSeat.amount()));
    }

    if (!newNonSeats.equals(currentNonSeats)) {
      this.bookingNonSeatRepository.deleteAllByBookingId(bookingId);
      this.bookingNonSeatRepository.saveAll(newNonSeats);
    }

    Booking booking = optionalBooking.get();
    booking.setBookedAt(Timestamp.from(Instant.now()));
    booking.setCost(reservationPurchaseDto.cost());
    bookingRepository.save(booking);

    if (invoiceRepository.existsByBookingIdAndInvoiceType(booking.getId(), InvoiceType.purchase)) {
      throw new AlreadyExistsException(
          "A purchase with booking id " + booking.getId() + " was already made");
    }

    checkPresence(applicationUserRepository, booking.getBookedBy(), "user");
    checkPaymentInfo(reservationPurchaseDto.paymentInfoDto(), booking.getBookedBy());

    Invoice invoice = new Invoice();
    invoice.setBookingId(bookingId);
    invoice.setPurchasedAt(Timestamp.from(Instant.now()));
    invoice.setInvoiceType(InvoiceType.purchase);

    invoiceRepository.save(invoice);

    return booking;
  }

  @Override
  public ReservationInfoDto getReservationInfo(Long bookingId) {
    LOGGER.debug("Getting information about reservation");

    Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
    if (!optionalBooking.isPresent()) {
      throw new NotFoundException(
          "Failed to load the page: the requested booking does not exist. Please contact our support team.");
    }
    Booking booking = optionalBooking.get();

    EventShowing showing = eventShowingRepository.findById(booking.getEventShowingId()).get();

    List<BookingSeat> seats = bookingSeatRepository.findAllByBookingId(bookingId);
    List<ReservationDto.Seat> insertSeats = new ArrayList<>();
    for (BookingSeat seat : seats) {
      ReservationDto.Seat insertSeat =
          new ReservationDto.Seat(
              seat.getSeatingPlanSeat(), seat.getSeatingPlanRow(), seat.getSeatingPlanSector());

      insertSeats.add(insertSeat);
    }

    List<BookingNonSeat> nonSeats = bookingNonSeatRepository.findAllByBookingId(bookingId);
    List<ReservationDto.NonSeat> insertNonSeats = new ArrayList<>();

    for (BookingNonSeat nonSeat : nonSeats) {
      ReservationDto.NonSeat insertNonSeat =
          new ReservationDto.NonSeat(nonSeat.getSeatingPlanSector(), nonSeat.getAmount());

      insertNonSeats.add(insertNonSeat);
    }

    ReservationDto bookingInfo =
        new ReservationDto(
            booking.getBookedBy(),
            showing.getPerformedAt(),
            booking.getCost(),
            insertSeats,
            insertNonSeats);

    ReservationInfoDto reservationInfoDto =
        new ReservationInfoDto(showing.getEventId(), showing.getId(), bookingInfo);

    return reservationInfoDto;
  }

  @Override
  public List<BookedSeat> findBookedSeats(Long showingId) {
    return bookingSeatRepository.findBookedSeatsByShowingId(showingId);
  }

  @Override
  public List<BookedNonSeat> findBookedNonSeats(Long showingId) {
    return bookingNonSeatRepository.findBookedNonSeatsByShowingId(showingId);
  }

  @Override
  public BookingOverviewDto cancelBooking(Long bookingId) {
    try {
      Booking booking = bookingRepository.findById(bookingId).orElse(null);
      Long paidInvoiceId = invoiceRepository.findInvoiceNumberByBookingIdAndPurchased(bookingId);
      if (booking == null) {
        return null;
      } else if (paidInvoiceId == null) {
        bookingRepository.deleteById(bookingId);
        return new BookingOverviewDto(
            bookingId, null, null, null, null, null, null, 0, null, BookingState.Reserved);
      }
      bookingRepository.cancelBooking(bookingId);
      Invoice cancelledInvoice = new Invoice();
      cancelledInvoice.setBookingId(bookingId);
      cancelledInvoice.setPurchasedAt(booking.getBookedAt());
      cancelledInvoice.setInvoiceType(InvoiceType.cancellation);
      invoiceRepository.save(cancelledInvoice);

      final var amountTickets =
          bookingSeatRepository.countByBookingId(booking.getId())
              + bookingNonSeatRepository.findAllByBookingId(booking.getId()).stream()
                  .map(BookingNonSeat::getAmount)
                  .mapToInt(Integer::intValue)
                  .sum();

      final var eventShowing =
          eventShowingRepository.findById(booking.getEventShowingId()).orElse(null);

      final var event = eventRepository.findById(eventShowing.getEventId()).orElse(null);
      final var localDateTime = eventShowing.getOccursOn().toLocalDateTime();
      final var dtfTime = DateTimeFormatter.ofPattern("HH:mm");
      final var dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

      final String date = localDateTime.toLocalDate().format(dtfDate);
      final String time =
          localDateTime.toLocalTime().format(dtfTime)
              + "-"
              + localDateTime
                  .toLocalTime()
                  .plus(event.getDuration().toHours(), ChronoUnit.HOURS)
                  .format(dtfTime);

      final String title = event.getTitle();
      final String base64 = imageService.imageToBase64(event.getImageRef());

      return new BookingOverviewDto(
          bookingId,
          paidInvoiceId,
          cancelledInvoice.getInvoiceNumber(),
          base64,
          title,
          date,
          time,
          amountTickets,
          booking.getCost(),
          BookingState.Cancelled);
    } catch (NullPointerException e) {
      return null;
    }
  }

  private void checkPaymentInfo(PaymentInfoDto paymentInfoDto, Long customerId) {
    boolean paymentInformationGiven =
        paymentInfoDto.cardNumber() != null
            && paymentInfoDto.cardExpirationDate() != null
            && paymentInfoDto.cardCvv() != null
            && paymentInfoDto.cardOwner() != null;

    if (!paymentInformationGiven) {
      if (!userService.paymentInfoExists(customerId)) {
        throw new MissingPaymentInformationException(
            "No valid payment information saved for this user");
      }
    } else if (paymentInformationGiven) {
      userService.validateExpirationDate(paymentInfoDto.cardExpirationDate());
    } else {
      throw new ValidationException("Payment information must not be null");
    }
  }

  private void checkPresence(JpaRepository repository, Object id, String entity) {
    if (!repository.existsById(id)) {
      throw new NotFoundException("Referenced " + entity + " with id " + id + " does not exist!");
    }
  }
}
