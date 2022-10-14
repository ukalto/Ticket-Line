package at.ac.tuwien.sepm.groupphase.backend.service.booking;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.BookingOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedNonSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedSeat;
import java.util.List;

public interface BookingService {

  /**
   * Creates a booking and invoice entry in the system and either a booking-seat or -non seat entry.
   *
   * @param reservationDto stores booking information
   * @return booking entity storing relevant information about the reservation
   */
  Booking reserve(ReservationDto reservationDto, Long eventShowingId);

  /**
   * Creates a booking and invoice entry in the system and either a booking-seat or -non seat entry.
   *
   * @param purchaseDto stores booking information as well as payment information
   * @return booking entity storing relevant information about the purchase
   */
  Booking purchase(PurchaseDto purchaseDto, Long eventShowingId);

  /**
   * Gets all information necessary to turn a reservation into a purchase.
   *
   * @param bookingId matching the reservation
   * @return information about the reservation
   */
  ReservationInfoDto getReservationInfo(Long bookingId);

  /**
   * Creates an invoice for type purchase for an already existing booking.
   *
   * @param bookingId referencing a booking being turned into a purchase
   * @param reservationPurchaseDto consists of booked seats, non seats and payment information
   * @return invoice entity storing relevant information about the purchase
   */
  Booking reservationToPurchase(Long bookingId, ReservationPurchaseDto reservationPurchaseDto);

  /**
   * Returns all seats, that are already booked for this showing.
   *
   * @param showingId id of the showing
   * @return List of all seats that were booked
   */
  List<BookedSeat> findBookedSeats(Long showingId);

  /**
   * Returns all non-seats, that are already booked for this showing.
   *
   * @param showingId id of the showing
   * @return List of all non-seats that were booked
   */
  List<BookedNonSeat> findBookedNonSeats(Long showingId);

  /**
   * Returns all bookings with bookedBy.
   *
   * @param bookedBy used for matching bookedBy in database
   * @return List of BookingDtos
   */
  List<BookingOverviewDto> findAllBookingsByBookedBy(Long bookedBy);

  /**
   * Returns details about the showing with the given id.
   *
   * @param id the showing id
   * @return Details to the showing
   */
  BookingShowingDetailsDto findShowingDetailsById(Long id);

  /**
   * Cancels one booking with the given bookingId and returns a BookingOverviewDto with the changed
   * is_canceled.
   *
   * @param bookingId the booking id
   * @return BookingOverviewDto
   */
  BookingOverviewDto cancelBooking(Long bookingId);
}
