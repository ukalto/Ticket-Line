package at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatingPlanRepository
    extends JpaRepository<SeatingPlan, Long>, CustomSeatingPlanRepository {

  /**
   * Finds all seating plans by attribute located in.
   *
   * @param locatedIn attribute used to find matching seating plans
   * @return matching seating plans
   */
  @Query(value = "SELECT * FROM SEATING_PLAN WHERE located_in = ?1", nativeQuery = true)
  List<SeatingPlan> findAllByLocatedIn(Long locatedIn);

  /**
   * Finds SeatingPlan with id.
   *
   * @param id of the SeatingPlan
   * @return SeatingPlan
   */
  Optional<SeatingPlan> findById(Long id);
}
