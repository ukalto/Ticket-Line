package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.Location")
@Table(name = "location")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "town", nullable = false)
  private String town;

  @Column(name = "street", nullable = false)
  private String street;

  @Column(name = "postal_code", nullable = false)
  private String postalCode;
}
