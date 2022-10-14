package at.ac.tuwien.sepm.groupphase.backend.service.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;
import java.util.List;

public interface SeatingPlanService {

  /**
   * Saves a seating plan.
   *
   * @param seatingPlanCreationDto to be saved
   * @return entity of saved seating plan
   */
  SeatingPlan save(SeatingPlanCreationDto seatingPlanCreationDto);

  /**
   * Finds all SeatingPlans within the given location ID in the locatedIn column.
   *
   * @param id id of the location
   * @return list of SeatingPlans
   */
  List<SeatingPlan> findSeatingPlansByLocation(Long id);

  /**
   * Finds SeatingPlan with the given ID.
   *
   * @param id id of the SeatingPlan
   * @return SeatingPlanResponseDto
   */
  SeatingPlanResponseDto findSeatingPlanById(Long id);
}
