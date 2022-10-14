package at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanRow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatingPlanRowRepository
    extends JpaRepository<SeatingPlanRow, SeatingPlanRow.PrimaryKeys> {

  /**
   * Finds SeatingPlanRows with the given SeatingPlan ID and SeatingPlanSector ID.
   *
   * @param planId id of the SeatingPlan
   * @param sectorId id of the SeatingPlanSector
   * @return list of SeatingPlanRows
   */
  List<SeatingPlanRow> findBySeatingPlanIdAndSeatingPlanSector(Long planId, Long sectorId);
}
