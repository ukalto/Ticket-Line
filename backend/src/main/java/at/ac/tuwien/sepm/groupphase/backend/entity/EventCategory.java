package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory")
@Table(name = "event_category")
public class EventCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "display_name", nullable = false)
  private String displayName;
}
