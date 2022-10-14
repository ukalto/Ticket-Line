package at.ac.tuwien.sepm.groupphase.backend.repository.booking;

import at.ac.tuwien.sepm.groupphase.backend.entity.BookingNonSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedNonSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingNonSeatRepository
    extends JpaRepository<BookingNonSeat, BookingNonSeat.PrimaryKeys> {

  /**
   * Returns a list of BookedNonSeats from the corresponding showing.
   *
   * @param showingId the id of the showing
   * @return list of BookedNonSeats
   */
  @Query(
      value =
          """
        SELECT seating_plan_sector as sector, amount FROM booking_non_seat
        INNER JOIN booking ON booking_non_seat.booking_id = booking.id
        WHERE event_showing_id = ?1
        """,
      nativeQuery = true)
  List<BookedNonSeat> findBookedNonSeatsByShowingId(Long showingId);

  /**
   * Returns the amount of seats with the corresponding showing and sector.
   *
   * @param showingId the id of the showing
   * @param sectorId the sector in the seating plan
   * @return amount of seats
   */
  @Query(
      value =
          """
        SELECT COALESCE(sum(amount),0) FROM booking_non_seat
        INNER JOIN booking ON booking_non_seat.booking_id = booking.id
        WHERE event_showing_id = ?1 AND seating_plan_sector= ?2
        """,
      nativeQuery = true)
  Long findBookedNonSeatsByShowingIdAndSectorId(Long showingId, Long sectorId);

  /**
   * Returns a list of all bookingNonSeats with the same bookingId.
   *
   * @param bookingId id of booking
   * @return List of bookingNonSeats
   */
  List<BookingNonSeat> findAllByBookingId(@Param("booking_id") Long bookingId);

  /**
   * Deletes all booked non seats with corresponding booking id.
   *
   * @param bookingId to match booked non seats
   * @return number of deleted entrys
   */
  long deleteAllByBookingId(Long bookingId);

  /**
   * Returns bookingNonSeat with given bookingId and seatingPlanSectorId.
   *
   * @param bookingId id of booking
   * @param seatingPlanSector id of seatingPlanSector
   * @return bookingNonSeat
   */
  BookingNonSeat findByBookingIdAndSeatingPlanSector(
      @Param("booking_id") Long bookingId, @Param("seating_plan_sector") Long seatingPlanSector);
}
