package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.BookingNonSeat")
@Table(name = "booking_non_seat")
@IdClass(BookingNonSeat.PrimaryKeys.class)
public class BookingNonSeat {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long bookingId;
    private Long seatingPlanSector;
    private Long seatingPlanId;
  }

  @Id
  @Column(name = "booking_id", nullable = false)
  private Long bookingId;

  @Id
  @Column(name = "seating_plan_sector", nullable = false)
  private Long seatingPlanSector;

  @Id
  @Column(name = "seating_plan_id", nullable = false)
  private Long seatingPlanId;

  @Column(name = "amount", nullable = false)
  private Integer amount;
}
