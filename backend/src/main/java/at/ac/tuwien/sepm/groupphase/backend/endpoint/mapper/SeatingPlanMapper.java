package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;
import org.springframework.stereotype.Component;

@Component
public class SeatingPlanMapper {

  public SeatingPlanDto entityToDto(SeatingPlan seatingPlan) {

    return new SeatingPlanDto(
        seatingPlan.getId(),
        seatingPlan.getName(),
        seatingPlan.getLocatedIn(),
        seatingPlan.getCapacity());
  }
}
