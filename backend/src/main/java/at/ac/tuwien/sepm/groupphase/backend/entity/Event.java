package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SqlResultSetMapping(
    name = "findTopEventsMapping",
    classes = {
      @ConstructorResult(
          targetClass = TopEvent.class,
          columns = {
            @ColumnResult(name = "event.id", type = BigInteger.class),
            @ColumnResult(name = "event.title", type = String.class),
            @ColumnResult(name = "event.category_id", type = BigInteger.class),
            @ColumnResult(name = "event.duration", type = BigInteger.class),
            @ColumnResult(name = "event.description", type = String.class),
            @ColumnResult(name = "total_bookings", type = BigDecimal.class)
          })
    })
@NamedNativeQuery(
    resultClass = TopEvent.class,
    resultSetMapping = "findTopEventsMapping",
    name = "Event.findTopEvents",
    query =
        """
  with event_booking_amounts (id, amount_bookings) as (
    select event.id, (
      select count(*)
        from booking_seat
       where booking_seat.booking_id = booking.id
    ) + (
    select coalesce(sum(amount), 0)
      from booking_non_seat
     where booking_non_seat.booking_id = booking.id
    ) as amount_bookings
      from event
      left join event_showing on event.id = event_showing.event_id
      left join booking on booking.event_showing_id = event_showing.id
     where (( ?1 is null ) or ( category_id = ?1 ))
)
select event.id,
       event.title,
       event.category_id,
       event.duration,
       event.description,
       sum(amount_bookings) as total_bookings
  from event_booking_amounts
  join event on event_booking_amounts.id = event.id
 group by event.id
 order by sum(amount_bookings) desc
 limit ?2
  """)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.Event")
@Table(name = "event")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(name = "duration", nullable = false)
  private Duration duration;

  @Column(name = "description", nullable = false, length = 1000)
  private String description;

  @Column(name = "image_ref", nullable = true)
  private String imageRef;
}
