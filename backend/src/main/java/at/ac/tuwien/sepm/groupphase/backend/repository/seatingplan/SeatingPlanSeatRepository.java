package at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatingPlanSeatRepository
    extends JpaRepository<SeatingPlanSeat, SeatingPlanSeat.PrimaryKeys> {

  /**
   * Finds all SeatingPlanSeats with the given SeatingPlanRow, SeatingPlanSector and SeatingPlan
   * IDs.
   *
   * @param planId id of SeatingPlan
   * @param sectorId id of SeatingPlanSector
   * @param rowId id of SeatingPlanRow
   * @return list of SeatingPlanSeats
   */
  List<SeatingPlanSeat> findBySeatingPlanIdAndSeatingPlanSectorAndSeatingPlanRow(
      Long planId, Long sectorId, Long rowId);
}
