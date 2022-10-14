package at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;

public interface CustomSeatingPlanRepository {
  /**
   * Saves a seating plan in the database.
   *
   * @param seatingPlanCreationDto to be saved
   * @return seating plan entity that was saved
   */
  SeatingPlan save(SeatingPlanCreationDto seatingPlanCreationDto);
}
