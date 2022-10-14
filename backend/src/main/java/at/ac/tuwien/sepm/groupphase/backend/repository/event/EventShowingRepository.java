package at.ac.tuwien.sepm.groupphase.backend.repository.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.EventShowing;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventShowingRepository extends JpaRepository<EventShowing, Long> {
  /**
   * Finds a page of EventShowings with the given event id and pageable informations.
   *
   * @param eventId used for matching EventShowing eventId in database
   * @param pageable used for paging
   * @return Page with EventShowing
   */
  Optional<Page<EventShowing>> findAllByEventId(Long eventId, Pageable pageable);

  /**
   * Finds an EventShowing with the given id if existing.
   *
   * @param id used for matching EventShowing id in database
   * @return EventShowing
   */
  Optional<EventShowing> findById(Long id);

  /**
   * Returns true if the seating plan is occupied at the given time window, false otherwise.
   *
   * @param seatingPlanId id of the checked seating plan
   * @param start starting time
   * @param end end time
   * @return true or false
   */
  @Query(
      value =
          "SELECT EXISTS (SELECT event_showing.id FROM event_showing INNER JOIN event ON event_id = event.id WHERE performed_at = ?1"
              + " AND (occurs_on <= ?2 AND occurs_on + (cast (duration as decimal)/86400000000000) >  ?2"
              + " OR occurs_on <= ?3 AND occurs_on + (cast (duration as decimal)/86400000000000) >  ?3))",
      nativeQuery = true)
  Boolean isOccupied(Long seatingPlanId, Timestamp start, Timestamp end);

  /**
   * Finds an EventShowing with the given id and event id.
   *
   * @param id Id of the show
   * @param eventId Id of the event
   * @return EventShowing
   */
  Optional<EventShowing> findByIdAndEventId(Long id, Long eventId);

  @Query(
      value =
          """
          select (
              (select capacity FROM SEATING_PLAN where id=?2)
              - (select COALESCE(sum(bns.amount),0) from booking_non_seat bns left join booking b on bns.booking_id=b.id WHERE event_showing_id=?1 AND is_cancelled=false)
              - (select count(*) FROM BOOKING_SEAT bs left join booking b1 on bs.booking_id=b1.id WHERE event_showing_id=?1 AND is_cancelled=false)
          ) vacant_tickets
        """,
      nativeQuery = true)
  Long getVacantTicketsByShowingId(Long showingId, Long seatingPlanId);

  /** Deletes all reservations for showings that occur in half an hour. */
  @Modifying
  @Transactional
  @Query(
      value =
          """
        DELETE FROM BOOKING_NON_SEAT BNS
        WHERE BNS.BOOKING_ID IN (
        SELECT B.ID FROM BOOKING B
        JOIN EVENT_SHOWING ES ON ES.ID = B.EVENT_SHOWING_ID
        LEFT JOIN INVOICE I ON I.BOOKING_ID = B.ID
        WHERE NOW() >= (ES.OCCURS_ON - (1.0/24/2)) AND INVOICE_NUMBER IS NULL
        );

        DELETE FROM BOOKING_SEAT BS
        WHERE BS.BOOKING_ID IN (
        SELECT B.ID FROM BOOKING B
        JOIN EVENT_SHOWING ES ON ES.ID = B.EVENT_SHOWING_ID
        LEFT JOIN INVOICE I ON I.BOOKING_ID = B.ID
        WHERE NOW() >= (ES.OCCURS_ON - (1.0/24/2)) AND INVOICE_NUMBER IS NULL
        );

        DELETE FROM BOOKING B
        WHERE B.ID IN (
        SELECT B2.ID FROM BOOKING B2
        JOIN EVENT_SHOWING ES ON ES.ID = B2.EVENT_SHOWING_ID
        LEFT JOIN INVOICE I ON I.BOOKING_ID = B2.ID
        WHERE NOW() >= (ES.OCCURS_ON - (1.0/24/2)) AND INVOICE_NUMBER IS NULL
        );
          """,
      nativeQuery = true)
  void deleteAllReservationsHalfAnHourBeforeShowing();

  @Query(
      value =
          "SELECT DISTINCT showing.* "
              + "FROM event_showing AS showing "
              + "LEFT JOIN event ON showing.event_id = event.id "
              + "LEFT JOIN seating_plan ON showing.performed_at = seating_plan.id "
              + "LEFT JOIN location ON seating_plan.located_in = location.id "
              + "JOIN seating_plan_sector AS sector ON showing.performed_at = sector.seating_plan_id "
              + "JOIN sector_price_event_showing AS price ON showing.id = price.event_showing_id AND showing.performed_at = price.seating_plan_id AND sector.number = price.seating_plan_sector "
              + "WHERE (?1 IS NULL OR UPPER(event.title) LIKE UPPER(CONCAT('%',?1,'%'))) "
              + "AND (?2 IS NULL OR UPPER(location.name) LIKE UPPER(CONCAT('%',?2,'%'))) "
              + "AND (?3 IS NULL OR FORMATDATETIME(showing.occurs_on,'yyyy-MM-dd') = ?3) "
              + "AND showing.occurs_on >= CURRENT_TIMESTAMP() "
              + "AND (?4 IS NULL OR showing.occurs_on >= "
              + "PARSEDATETIME(CONCAT_WS(' ', FORMATDATETIME(showing.occurs_on,'yyyy-MM-dd'), ?4), 'yyyy-MM-dd HH:mm:ss')) "
              + "AND (?5 IS NULL OR DATEADD('NANOSECOND', event.duration, showing.occurs_on) <= "
              + "PARSEDATETIME(CONCAT_WS(' ', FORMATDATETIME((CASE WHEN FORMATDATETIME(showing.occurs_on, 'HH:mm:ss') < ?5 THEN showing.occurs_on "
              + "ELSE DATEADD('NANOSECOND', event.duration, showing.occurs_on) END),'yyyy-MM-dd'), ?5), 'yyyy-MM-dd HH:mm:ss')) "
              + "AND (?6 IS NULL OR price.price >= ?6) "
              + "AND (?7 IS NULL OR price.price <= ?7)",
      countQuery =
          "SELECT COUNT(DISTINCT showing.id) "
              + "FROM event_showing AS showing "
              + "LEFT JOIN event ON showing.event_id = event.id "
              + "LEFT JOIN seating_plan ON showing.performed_at = seating_plan.id "
              + "LEFT JOIN location ON seating_plan.located_in = location.id "
              + "JOIN seating_plan_sector AS sector ON showing.performed_at = sector.seating_plan_id "
              + "JOIN sector_price_event_showing AS price ON showing.id = price.event_showing_id AND showing.performed_at = price.seating_plan_id AND sector.number = price.seating_plan_sector "
              + "WHERE (?1 IS NULL OR UPPER(event.title) LIKE UPPER(CONCAT('%',?1,'%'))) "
              + "AND (?2 IS NULL OR UPPER(location.name) LIKE UPPER(CONCAT('%',?2,'%'))) "
              + "AND (?3 IS NULL OR FORMATDATETIME(showing.occurs_on,'yyyy-MM-dd') = ?3) "
              + "AND showing.occurs_on >= CURRENT_TIMESTAMP() "
              + "AND (?4 IS NULL OR showing.occurs_on >= "
              + "PARSEDATETIME(CONCAT_WS(' ', FORMATDATETIME(showing.occurs_on,'yyyy-MM-dd'), ?4), 'yyyy-MM-dd HH:mm:ss')) "
              + "AND (?5 IS NULL OR DATEADD('NANOSECOND', event.duration, showing.occurs_on) <= "
              + "PARSEDATETIME(CONCAT_WS(' ', FORMATDATETIME((CASE WHEN FORMATDATETIME(showing.occurs_on, 'HH:mm:ss') < ?5 THEN showing.occurs_on "
              + "ELSE DATEADD('NANOSECOND', event.duration, showing.occurs_on) END),'yyyy-MM-dd'), ?5), 'yyyy-MM-dd HH:mm:ss')) "
              + "AND (?6 IS NULL OR price.price >= ?6) "
              + "AND (?7 IS NULL OR price.price <= ?7)",
      nativeQuery = true)
  Page<EventShowing> findFiltered(
      String eventTitle,
      String locationId,
      LocalDate date,
      LocalTime startTime,
      LocalTime endTime,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Pageable pageable);
}
