package at.ac.tuwien.sepm.groupphase.backend.repository.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
  /**
   * Finds an event with the given id if existing.
   *
   * @param id used for matching event id in database
   * @return event if event exists with id if not null
   */
  Optional<Event> findById(Long id);

  /**
   * Returns all events ordered by their title.
   *
   * @return list of events
   */
  List<Event> findAllByOrderByTitleDesc();

  /**
   * Returns all Events, that fit the search criteria.
   *
   * @param titleOrDescription the title or the description of the event
   * @param categoryId the id of the category of the event
   * @param minDuration the minimum duration of the event
   * @param maxDuration the maximum duration of the event
   * @param pageNumber the number of the page, that should be loaded
   * @param pageSize the amount of events per page
   * @return a List of events
   */
  @Query(
      value =
          """
          SELECT * FROM EVENT WHERE
          ((UPPER(title) LIKE UPPER(CONCAT('%',?1,'%')) OR ?1 IS NULL)
          OR (UPPER(description) LIKE UPPER(CONCAT('%',?1,'%')) OR ?1 IS NULL))
          AND (category_id = ?2 OR ?2 IS NULL)
          AND ((duration between ?3 and ?4) OR ?3 IS NULL OR ?4 IS NULL)
          ORDER BY title OFFSET ?5 * ?6 ROWS FETCH NEXT ?6 ROWS ONLY
        """,
      nativeQuery = true)
  List<Event> filterEvents(
      String titleOrDescription,
      Long categoryId,
      Duration minDuration,
      Duration maxDuration,
      Long pageNumber,
      Long pageSize);

  /**
   * Returns top 3 events matching the title.
   *
   * @param title title of the event
   * @return List of top 3 events
   */
  List<Event> findTop3ByTitleContainingAllIgnoreCase(@Param("title") String title);

  /**
   * Changes imageRef from event with the given event id .
   *
   * @param id id of the event
   * @param imageRef of the image of the event
   */
  @Modifying
  @Query(value = "UPDATE Event e SET e.image_ref =:imageRef where e.id =:id", nativeQuery = true)
  void changeImageRefFromEventWithEventId(@Param("id") Long id, @Param("imageRef") String imageRef);
}
