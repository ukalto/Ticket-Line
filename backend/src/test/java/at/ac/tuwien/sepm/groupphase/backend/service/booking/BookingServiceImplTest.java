package at.ac.tuwien.sepm.groupphase.backend.service.booking;

import static org.mockito.Mockito.mock;

import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingNonSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.BookingSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventShowingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingServiceImplTest {
  private BookingRepository bookingRepository;
  private BookingSeatRepository bookingSeatRepository;
  private BookingNonSeatRepository bookingNonSeatRepository;
  private InvoiceRepository invoiceRepository;
  private ApplicationUserRepository applicationUserRepository;
  private EventShowingRepository eventShowingRepository;
  private SeatingPlanRepository seatingPlanRepository;
  private SeatingPlanSectorRepository seatingPlanSectorRepository;
  private SeatingPlanSeatRepository seatingPlanSeatRepository;
  private SeatingPlanRowRepository seatingPlanRowRepository;
  private UserService userService;

  private BookingService bookingService;

  @BeforeEach
  void setup() {
    this.bookingRepository = mock(BookingRepository.class);
    this.bookingSeatRepository = mock(BookingSeatRepository.class);
    this.bookingNonSeatRepository = mock(BookingNonSeatRepository.class);
    this.invoiceRepository = mock(InvoiceRepository.class);
    this.applicationUserRepository = mock(ApplicationUserRepository.class);
    this.eventShowingRepository = mock(EventShowingRepository.class);
    this.seatingPlanRepository = mock(SeatingPlanRepository.class);
    this.seatingPlanSectorRepository = mock(SeatingPlanSectorRepository.class);
    this.seatingPlanSeatRepository = mock(SeatingPlanSeatRepository.class);
    this.seatingPlanRowRepository = mock(SeatingPlanRowRepository.class);
    this.userService = mock(UserService.class);

    //    this.bookingService =
    //        new BookingServiceImpl(
    //            this.bookingRepository,
    //            this.bookingSeatRepository,
    //            this.bookingNonSeatRepository,
    //            this.invoiceRepository,
    //            this.applicationUserRepository,
    //            this.eventShowingRepository,
    //            this.seatingPlanRepository,
    //            this.seatingPlanSectorRepository,
    //            this.seatingPlanSeatRepository,
    //            this.seatingPlanRowRepository,
    //            this.userService);
  }

  @Test
  void reserveWithCorrectValues_shouldWork() {
    //    List<ReservationDto.Seat> oneBookedSeat = new ArrayList<>();
    //    oneBookedSeat.add(new ReservationDto.Seat(0L, 0L, 0L));
    //
    //    List<ReservationDto.NonSeat> oneBookedNonSeatSector = new ArrayList<>();
    //    oneBookedNonSeatSector.add(new ReservationDto.NonSeat(-1L, 5));
    //
    //    this.bookingService.reserve(
    //        new ReservationDto(-3L, -1L, new BigDecimal(10), oneBookedSeat,
    // oneBookedNonSeatSector),
    //        -4L);
  }

  @Test
  void reserveWithMissingValues_shouldThrow() {}

  @Test
  void reserveWithIncorrectValues_shouldThrow() {}

  @Test
  void purchaseWithCorrectValues_shouldWork() {}

  @Test
  void purchaseWithMissingValues_shouldThrow() {}

  @Test
  void purchaseWithIncorrectValues_shouldThrow() {}

  @Test
  void reservationToPurchaseWithCorrectValues_shouldWork() {}

  @Test
  void reservationToPurchaseWithMissingValues_shouldThrow() {}

  @Test
  void reservationToPurchaseWithIncorrectValues_shouldThrow() {}
}
