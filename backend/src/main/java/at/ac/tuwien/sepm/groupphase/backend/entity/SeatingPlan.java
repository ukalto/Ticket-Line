package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.ShowRoom")
@Table(name = "seating_plan")
public class SeatingPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "located_in", nullable = false)
  private Long locatedIn;

  @Column(name = "capacity", nullable = false)
  private Integer capacity;
}
