package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsEntryRepository extends JpaRepository<NewsEntry, Long> {
  /**
   * Finds a NewsEntry with the given id if existing.
   *
   * @param id used for matching NewsEntry id in database
   * @return NewsEntry or null
   */
  NewsEntry findNewsEntryById(Long id);

  /**
   * Find all message entries ordered by published at date (descending).
   *
   * @return ordered list of al message entries
   */
  List<NewsEntry> findAllByOrderByPublishedOnDesc();

  /**
   * Finds all unread news from a specific user.
   *
   * @param userId id of the user
   * @return List of NewsEntries
   */
  @Query(
      value =
          "SELECT id, title, contents, summary, published_on, published_by, image_ref, event_id FROM news_entry"
              + " WHERE id NOT IN (SELECT id FROM news_entry INNER JOIN news_entry_read_by ON id = news_id WHERE application_user_id = ?1)"
              + " ORDER BY published_on DESC",
      nativeQuery = true)
  List<NewsEntry> findUnreadNews(Long userId);

  /**
   * Finds all read news from a specific user.
   *
   * @param userId id of the user
   * @return List of NewsEntries
   */
  @Query(
      value =
          "SELECT id, title, contents, summary, published_on, published_by, image_ref, event_id FROM news_entry INNER JOIN news_entry_read_by"
              + " ON id = news_id"
              + " WHERE application_user_id = ?1"
              + " ORDER BY published_on DESC",
      nativeQuery = true)
  List<NewsEntry> findReadNews(Long userId);

  /**
   * Changes imageRef from newsEntry with the given newsEntry id .
   *
   * @param id id of the newsEntry
   * @param imRef of the image of the newsEntry
   */
  @Modifying
  @Transactional
  @Query(
      value = "UPDATE news_entry n SET n.image_ref =:imageRef where n.id =:id",
      nativeQuery = true)
  void changeImageRefFromNewsEntryWithNewsEntryId(
      @Param("id") Long id, @Param("imageRef") String imRef);
}
