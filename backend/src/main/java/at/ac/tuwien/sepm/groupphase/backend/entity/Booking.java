package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.Booking")
@Table(name = "booking")
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "booked_by", nullable = false)
  private Long bookedBy;

  @Column(name = "booked_at", nullable = false)
  private Timestamp bookedAt;

  @Column(name = "event_showing_id", nullable = false)
  private Long eventShowingId;

  @Column(name = "is_cancelled", nullable = false)
  private boolean isCancelled;

  @Column(name = "cost", nullable = false)
  private BigDecimal cost;

  @Column(name = "secret", nullable = false)
  // default value will be a UUIDv4
  private String secret = UUID.randomUUID().toString();
}
