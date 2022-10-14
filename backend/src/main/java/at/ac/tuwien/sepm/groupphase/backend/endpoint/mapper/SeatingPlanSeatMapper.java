package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanSeat;
import org.mapstruct.Mapper;

@Mapper
public interface SeatingPlanSeatMapper {

  SeatingPlanResponseDto.Seat seatingPlanSeatToSeatDto(SeatingPlanSeat seatingPlanSeat);
}
