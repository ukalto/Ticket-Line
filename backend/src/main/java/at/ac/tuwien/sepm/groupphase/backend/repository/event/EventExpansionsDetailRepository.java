package at.ac.tuwien.sepm.groupphase.backend.repository.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.custom.EventExpansionDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventExpansionsDetailRepository extends JpaRepository<Event, Long> {

  /**
   * Finds the Events of a location that have showings in the present, and that have showings with
   * tickets left.
   *
   * @param locationId identifier of the location
   * @return An EventExpansionDetail object, that holds all necessary information
   *     (id,title,description,category,imageRef,soldout)
   */
  @Query(
      value =
          "SELECT LE.ID, LE.TITLE, LE.DESCRIPTION, LE.IMAGEREFERENCE, LE.CATEGORY, "
              + "CASE WHEN ((SUM(LE.AMOUNT)+SUM(LE.SEATS)) >= SUM(LE.CAPACITY))  THEN TRUE ELSE FALSE END AS SOLDOUT "
              + "FROM (SELECT E.ID as ID, E.TITLE as TITLE, E.DESCRIPTION as DESCRIPTION, E.IMAGE_REF as IMAGEREFERENCE, "
              + "EC.DISPLAY_NAME AS CATEGORY, SUM(BSN.AMOUNT) as AMOUNT, SUM(BSN.SEATS) as SEATS, SUM(DISTINCT CAPACITY) as CAPACITY "
              + "FROM EVENT_SHOWING ES "
              + "LEFT JOIN BOOKING B ON B.EVENT_SHOWING_ID = ES.ID "
              + "LEFT JOIN (SELECT BOOKING_ID,AMOUNT, 0 as SEATS FROM BOOKING_NON_SEAT "
              + "UNION "
              + "SELECT BOOKING_ID, 0, COUNT(BOOKING_ID) FROM BOOKING_SEAT "
              + "GROUP BY BOOKING_ID) BSN ON BSN.BOOKING_ID = B.ID "
              + "LEFT JOIN SEATING_PLAN SP ON SP.ID = ES.PERFORMED_AT "
              + "JOIN EVENT E ON E.ID = ES.EVENT_ID "
              + "JOIN EVENT_CATEGORY EC ON EC.ID = E.CATEGORY_ID "
              + "WHERE (B.IS_CANCELLED IS NULL OR B.IS_CANCELLED = FALSE) AND ES.OCCURS_ON > NOW() AND SP.LOCATED_IN = ?1 "
              + "GROUP BY ES.ID, E.ID) AS LE "
              + "GROUP BY LE.ID "
              + "ORDER BY SOLDOUT",
      nativeQuery = true)
  List<EventExpansionDetails> findEventsForLocation(Long locationId);

  /**
   * Finds the Events of a artist that have showings in the present, and that have showings with
   * tickets left.
   *
   * @param artistId identifier of the artist
   * @return An EventExpansionDetail object, that holds all necessary information
   *     (id,title,description,category,imageRef,soldout)
   */
  @Query(
      value =
          "SELECT LE.ID, LE.TITLE, LE.DESCRIPTION, LE.IMAGEREFERENCE, LE.CATEGORY, "
              + "CASE WHEN ((SUM(LE.AMOUNT)+SUM(LE.SEATS)) >= SUM(LE.CAPACITY))  THEN TRUE ELSE FALSE END AS SOLDOUT "
              + "FROM (SELECT E.ID as ID, E.TITLE as TITLE, E.DESCRIPTION as DESCRIPTION, E.IMAGE_REF as IMAGEREFERENCE, "
              + "EC.DISPLAY_NAME AS CATEGORY, SUM(BSN.AMOUNT) as AMOUNT, SUM(BSN.SEATS) as SEATS, SUM(DISTINCT CAPACITY) as CAPACITY "
              + "FROM EVENT_SHOWING ES "
              + "JOIN ARTIST_PERFORMANCE AP ON E.ID = AP.EVENT_ID "
              + "LEFT JOIN BOOKING B ON B.EVENT_SHOWING_ID = ES.ID "
              + "LEFT JOIN (SELECT BOOKING_ID,AMOUNT, 0 as SEATS FROM BOOKING_NON_SEAT "
              + "UNION "
              + "SELECT BOOKING_ID, 0, COUNT(BOOKING_ID) FROM BOOKING_SEAT "
              + "GROUP BY BOOKING_ID) BSN ON BSN.BOOKING_ID = B.ID "
              + "LEFT JOIN SEATING_PLAN SP ON SP.ID = ES.PERFORMED_AT "
              + "JOIN EVENT E ON E.ID = ES.EVENT_ID "
              + "JOIN EVENT_CATEGORY EC ON EC.ID = E.CATEGORY_ID "
              + "WHERE (B.IS_CANCELLED IS NULL OR B.IS_CANCELLED = FALSE) AND ES.OCCURS_ON > NOW() AND AP.ARTIST_ID = ?1 "
              + "GROUP BY ES.ID) AS LE "
              + "GROUP BY LE.ID "
              + "ORDER BY SOLDOUT",
      nativeQuery = true)
  List<EventExpansionDetails> findEventsForArtist(Long artistId);
}
