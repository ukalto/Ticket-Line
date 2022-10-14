package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.ArtistPerformance")
@Table(name = "artist_performance")
@IdClass(ArtistPerformance.PrimaryKeys.class)
public class ArtistPerformance {

  @Data
  public static class PrimaryKeys implements Serializable {
    private Long artistId;
    private Long eventId;
  }

  @Id
  @Column(name = "artist_id", nullable = false)
  private Long artistId;

  @Id
  @Column(name = "event_id", nullable = false)
  private Long eventId;
}
