package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan;

import at.ac.tuwien.sepm.groupphase.backend.entity.SectorType;
import java.util.List;
import javax.validation.constraints.*;

public record SeatingPlanCreationDto(
    @NotBlank(message = "Seating plan name must not be null or blank") @Size(max = 100) String name,
    @NotNull(message = "LocatedIn must not be null") Long locatedIn,
    @NotNull(message = "Seating plan capacity must not be null") Integer capacity,
    @NotEmpty(message = "There has to be at least one sector within a seating plan")
        List<Sector> sectors) {

  public record Sector(
      @NotBlank(message = "Sector name must not be null or blank") @Size(max = 100) String name,
      @NotBlank(message = "Color must not be null or blank")
          @Size(max = 50)
          @Pattern(
              regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$",
              message = "Color must be given as hex color")
          String color,
      @NotNull(message = "Sector capacity must not be null") Integer capacity,
      @NotNull(message = "Type must not be null") SectorType type,
      List<Row> rows) {}

  public record Row(
      @NotEmpty(message = "There has to be at least one seat within a row")
          List<@NotNull(message = "Seat must not be null") Seat> seats) {}

  public record Seat(@NotNull(message = "Enabled must not be null") Boolean enabled) {}
}
