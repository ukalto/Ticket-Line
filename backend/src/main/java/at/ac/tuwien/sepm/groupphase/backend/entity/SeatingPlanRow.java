package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.ShowRoomRow")
@Table(name = "seating_plan_row")
@IdClass(SeatingPlanRow.PrimaryKeys.class)
public class SeatingPlanRow {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long number;
    private Long seatingPlanId;
    private Long seatingPlanSector;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "number", nullable = false)
  private Long number;

  @Id
  @Column(name = "seating_plan_id")
  private Long seatingPlanId;

  @Id
  @Column(name = "seating_plan_sector")
  private Long seatingPlanSector;
}
