package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.EventShowing")
@Table(name = "event_showing")
public class EventShowing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "occurs_on", nullable = false)
  private Timestamp occursOn;

  @Column(name = "performed_at", nullable = false)
  private Long performedAt;

  @Column(name = "event_id", nullable = false)
  private Long eventId;
}
