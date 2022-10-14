package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.BookingSeat")
@Table(name = "booking_seat")
@IdClass(BookingSeat.PrimaryKeys.class)
public class BookingSeat {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long bookingId;
    private Long seatingPlanSeat;
    private Long seatingPlanRow;
    private Long seatingPlanId;
    private Long seatingPlanSector;
  }

  @Id
  @Column(name = "booking_id", nullable = false)
  private Long bookingId;

  @Id
  @Column(name = "seating_plan_seat", nullable = false)
  private Long seatingPlanSeat;

  @Id
  @Column(name = "seating_plan_row", nullable = false)
  private Long seatingPlanRow;

  @Id
  @Column(name = "seating_plan_id", nullable = false)
  private Long seatingPlanId;

  @Id
  @Column(name = "seating_plan_sector", nullable = false)
  private Long seatingPlanSector;
}
