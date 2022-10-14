package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.SectorPriceEventShowing")
@Table(name = "sector_price_event_showing")
@IdClass(SectorPriceEventShowing.PrimaryKeys.class)
public class SectorPriceEventShowing {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long eventShowingId;
    private Long seatingPlanId;
    private Long seatingPlanSector;
  }

  @Id
  @Column(name = "event_showing_id", nullable = false)
  private Long eventShowingId;

  @Id
  @Column(name = "seating_plan_id", nullable = false)
  private Long seatingPlanId;

  @Id
  @Column(name = "seating_plan_sector", nullable = false)
  private Long seatingPlanSector;

  @Column(name = "price", nullable = true)
  private java.math.BigDecimal price;
}
