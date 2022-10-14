package at.ac.tuwien.sepm.groupphase.backend.repository.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

  /**
   * Finds all EventCategories ordered by their display name.
   *
   * @return list of all EventCategories
   */
  List<EventCategory> findAllByOrderByDisplayName();
}
