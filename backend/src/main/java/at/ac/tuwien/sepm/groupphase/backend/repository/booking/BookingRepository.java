package at.ac.tuwien.sepm.groupphase.backend.repository.booking;

import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  /**
   * Gets a list of bookings with bookedBy.
   *
   * @param bookedBy used for matching bookedBy in database
   * @return list of bookings
   */
  List<Booking> findAllByBookedBy(@Param("booked_by") Long bookedBy);

  /**
   * Returns list with all booking which are not cancelled.
   *
   * @param eventShowingId id of eventShowing
   * @return List of bookings
   */
  @Query(
      value = "SELECT * FROM booking where event_showing_id =?1 and is_cancelled = FALSE",
      nativeQuery = true)
  List<Booking> findActiveBookingsForShowing(@Param("event_showing_id") Long eventShowingId);

  /**
   * Sets is cancelled to true for the right booking.
   *
   * @param id id booking
   */
  @Modifying
  @Transactional
  @Query(value = "UPDATE Booking b SET b.is_cancelled = TRUE where b.id =?1", nativeQuery = true)
  void cancelBooking(@Param("id") Long id);
}
