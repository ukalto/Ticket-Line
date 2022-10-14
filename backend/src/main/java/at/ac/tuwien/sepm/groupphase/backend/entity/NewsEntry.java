package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.sql.*;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry")
@Table(name = "news_entry")
public class NewsEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "contents", nullable = false, length = 3000)
  private String contents;

  @Column(name = "summary", nullable = true, length = 400)
  private String summary;

  @Column(name = "published_on", nullable = false)
  private Timestamp publishedOn;

  @Column(name = "published_by", nullable = false)
  private Long publishedBy;

  @Column(name = "image_ref", nullable = true)
  private String imageRef;

  @Column(name = "event_id", nullable = false)
  private Long eventId;
}
