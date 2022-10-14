package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record ReservationPurchaseDto(
    @NotNull(message = "must not be null") Long seatingPlanId,
    List<@NotNull(message = "must not be null") Seat> bookedSeats,
    List<@NotNull(message = "must not be null") NonSeat> bookedNonSeats,
    @NotNull(message = "must not be null") BigDecimal cost,
    @NotNull(message = "must not be null") PaymentInfoDto paymentInfoDto) {

  public record Seat(@NotNull Long seat, @NotNull Long row, @NotNull Long sector) {}

  public record NonSeat(
      @NotNull Long sector,
      @Pattern(regexp = "^[1-9]+[0-9]*$", message = "must be a positive number") Integer amount) {}
}
