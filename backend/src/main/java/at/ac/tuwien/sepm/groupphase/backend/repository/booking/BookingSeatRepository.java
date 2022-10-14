package at.ac.tuwien.sepm.groupphase.backend.repository.booking;

import at.ac.tuwien.sepm.groupphase.backend.entity.BookingSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedSeat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeat.PrimaryKeys> {

  /**
   * Returns all BookedSeats from the given showing.
   *
   * @param showingId the id of the showing
   * @return a list of BookedSeats.
   */
  @Query(
      value =
          """
      SELECT seating_plan_sector as sector, seating_plan_row as seatingPlanRow, seating_plan_seat as seat FROM booking_seat
      INNER JOIN booking ON booking_seat.booking_id = booking.id
      WHERE event_showing_id = ?1
      """,
      nativeQuery = true)
  List<BookedSeat> findBookedSeatsByShowingId(Long showingId);

  /**
   * Returns the booked seat with the given showing, sector, row and seat-number, if it exists.
   *
   * @param showingId the id of the showing
   * @param sector the id of the sector
   * @param row the id of the row
   * @param seat the id of the seat
   * @return a BookedSeat
   */
  @Query(
      value =
          """
      SELECT seating_plan_sector, seating_plan_row as seatingPlanRow, seating_plan_seat as seat FROM booking_seat
      INNER JOIN booking ON booking_seat.booking_id = booking.id
      WHERE event_showing_id = ?1 AND seating_plan_sector= ?2 AND seating_plan_row = ?3 AND seating_plan_seat = ?4
      """,
      nativeQuery = true)
  Optional<BookedSeat> findBookedSeatByShowingAndByPosition(
      Long showingId, Long sector, Long row, Long seat);

  /**
   * Returns number of bookedSeats with the same bookingId.
   *
   * @param bookingId id of booking
   * @return counted bookingSeats
   */
  int countByBookingId(Long bookingId);

  /**
   * Returns number of bookedSeats with the same bookingId and the same seatingPlanSectorId.
   *
   * @param bookingId id of booking
   * @param seatingPlanSectorId id of seatingPlanSector
   * @return counted bookingSeats
   */
  int countByBookingIdAndSeatingPlanSector(Long bookingId, Long seatingPlanSectorId);

  /**
   * Find all booked seats matching given booking id.
   *
   * @param bookingId used to filter non seats part of certain booking
   * @return matching seat bookings
   */
  List<BookingSeat> findAllByBookingId(Long bookingId);

  /**
   * Deletes all booked seats with corresponding booking id.
   *
   * @param bookingId to match booked seats
   * @return number of deleted entrys
   */
  long deleteAllByBookingId(Long bookingId);
}
