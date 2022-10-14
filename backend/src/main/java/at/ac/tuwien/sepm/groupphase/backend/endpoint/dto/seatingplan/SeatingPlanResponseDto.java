package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.entity.SectorType;
import java.util.List;

public record SeatingPlanResponseDto(
    String name, Long locatedIn, Integer capacity, List<Sector> sectors) {

  public record Sector(
      Long id, String name, String color, Integer capacity, SectorType type, List<Row> rows) {}

  public record Row(List<Seat> seats) {}

  public record Seat(Boolean enabled) {}
}
