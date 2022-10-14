package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanSeat")
@Table(name = "seating_plan_seat")
@IdClass(SeatingPlanSeat.PrimaryKeys.class)
public class SeatingPlanSeat {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long number;
    private Long seatingPlanRow;
    private Long seatingPlanId;
    private Long seatingPlanSector;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "number", nullable = false)
  private Long number;

  @Id
  @Column(name = "seating_plan_row", nullable = false)
  private Long seatingPlanRow;

  @Id
  @Column(name = "seating_plan_id", nullable = false)
  private Long seatingPlanId;

  @Id
  @Column(name = "seating_plan_sector", nullable = false)
  private Long seatingPlanSector;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled;
}
