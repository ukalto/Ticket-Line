package at.ac.tuwien.sepm.groupphase.backend.repository.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.TopEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopEventRepository extends JpaRepository<Event, Long> {
  /**
   * Lists the top N events by purchases filtered by category.
   *
   * @param categoryId to filter for.
   * @param topN determines how big the list may get.
   * @return the list of topN events.
   */
  @Query(nativeQuery = true)
  List<TopEvent> findTopEvents(Long categoryId, int topN);
}
