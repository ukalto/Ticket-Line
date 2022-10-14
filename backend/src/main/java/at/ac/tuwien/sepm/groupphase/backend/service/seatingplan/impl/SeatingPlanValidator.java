package at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorType;
import java.util.List;
import javax.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class SeatingPlanValidator {

  public void validateSeatingPlanCreationDto(SeatingPlanCreationDto seatingPlanCreationDto) {
    List<SeatingPlanCreationDto.Sector> sectors = seatingPlanCreationDto.sectors();

    for (SeatingPlanCreationDto.Sector sector : sectors) {
      boolean seatsInStandingSector =
          sector.type().equals(SectorType.standing)
              && (sector.rows() != null && !sector.rows().isEmpty());
      boolean noSeatsInSeatingSector =
          sector.type().equals(SectorType.seating)
              && (sector.rows() == null || sector.rows().isEmpty());

      if (seatsInStandingSector) {
        throw new ValidationException("In a standing sectorId rows must be null");
      }

      if (noSeatsInSeatingSector) {
        throw new ValidationException(
            "There has to be at least one seat within a seating sectorId");
      }
    }
  }
}
