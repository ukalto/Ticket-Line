package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopEvent {
  public Long id;
  public String title;
  public Long categoryId;
  public Duration duration;
  public String description;
  public Long totalBookings;

  public TopEvent(
      BigInteger id,
      String title,
      BigInteger categoryId,
      BigInteger duration,
      String description,
      BigDecimal totalBookings) {
    this.id = id.longValue();
    this.title = title;
    this.categoryId = categoryId.longValue();
    this.duration = Duration.ofSeconds(duration.longValue());
    this.description = description;
    this.totalBookings = totalBookings.longValue();
  }
}
