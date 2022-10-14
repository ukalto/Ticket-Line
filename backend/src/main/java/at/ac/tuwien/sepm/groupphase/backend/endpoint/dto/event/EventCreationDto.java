package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import java.sql.Timestamp;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record EventCreationDto(
    @NotBlank(message = "Title must not be blank") String title,
    @NotNull(message = "Category must not be empty") Long categoryId,
    @NotNull(message = "Duration must not be blank") Duration duration,
    @NotBlank(message = "Description must not be blank") String description,
    @NotEmpty(message = "There must be at least one artist")
        List<@NotNull(message = "There must be at least one artist") Long> artistIds,
    @NotEmpty(message = "There must be at least one show")
        List<@NotNull(message = "The shows must be complete") Showing> showings,
    String imageRef) {

  public record Duration(
      @NotNull(message = "Hours may not be empty") Long hours,
      @NotNull(message = "Minutes may not be empty") Long minutes) {}

  public record Showing(
      @NotEmpty(message = "Starting time must not be empty") Timestamp occursOn,
      @NotNull(message = "Seating plan must not be empty") Long performedAt,
      @NotNull(
              message = "There must be exactly one price per sectorId in the selected seating plan")
          List<@NotNull(message = "Pricings must not be empty") Pricing> pricings) {}

  public record Pricing(
      @NotNull(message = "Priced sectorId must not be empty") Long sectorId,
      java.math.BigDecimal price) {}
}
