package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntryReadBy")
@Table(name = "news_entry_read_by")
@IdClass(NewsEntryReadBy.PrimaryKeys.class)
public class NewsEntryReadBy {
  @Data
  public static class PrimaryKeys implements Serializable {
    private Long applicationUserId;
    private Long newsId;
  }

  @Id
  @Column(name = "application_user_id", nullable = false)
  private Long applicationUserId;

  @Id
  @Column(name = "news_id", nullable = false)
  private Long newsId;

  @Column(name = "visited", nullable = false)
  private Timestamp visited;
}
