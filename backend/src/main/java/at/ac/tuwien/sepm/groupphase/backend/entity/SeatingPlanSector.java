package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.ShowRoomSector")
@Table(name = "seating_plan_sector")
@IdClass(SeatingPlanSector.PrimaryKeys.class)
public class SeatingPlanSector {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long number;
    private Long seatingPlan;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "number", nullable = false)
  private Long number;

  @Id
  @ManyToOne
  @JoinColumn(name = "seating_plan_id")
  private SeatingPlan seatingPlan;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "color", nullable = false)
  private String color;

  @Column(name = "capacity", nullable = false)
  private Integer capacity;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private SectorType type;
}
