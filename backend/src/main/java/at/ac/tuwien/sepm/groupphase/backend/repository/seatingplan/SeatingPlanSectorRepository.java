package at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanSector;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatingPlanSectorRepository
    extends JpaRepository<SeatingPlanSector, SeatingPlanSector.PrimaryKeys> {
  /**
   * Finds SeatingPlanSectors with seatingPlanId.
   *
   * @param seatingPlanId id of the SeatingPlan
   * @return List of SeatingPlanSector
   */
  List<SeatingPlanSector> findAllBySeatingPlanId(Long seatingPlanId);

  /**
   * Returns the seating plan sector with the given seating plan and id.
   *
   * @param seatingPlanId the id of the corresponding seating plan
   * @param number the id of the sector
   * @return the SeatingPlanSector
   */
  SeatingPlanSector findBySeatingPlanIdAndNumber(Long seatingPlanId, Long number);
}
