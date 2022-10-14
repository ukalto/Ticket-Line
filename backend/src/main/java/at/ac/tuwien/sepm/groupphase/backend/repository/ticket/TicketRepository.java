package at.ac.tuwien.sepm.groupphase.backend.repository.ticket;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Invoice, Long> {
  /**
   * Queries the TicketSeatInfo associated with the invoice.
   *
   * @param invoiceId to query for.
   * @return the TicketSeatInfo.
   */
  @Query(
      value =
          """
      select
      event.title as eventTitle,
      1 as quantity,
      location.name as location,
      seating_plan.name as room,
      seating_plan_sector.name as sector,
      sector_price_event_showing.price as price,
     concat(
      'Sector ',
      booking_seat.seating_plan_sector,
      ' / Row ',
      booking_seat.seating_plan_row,
      ' / Seat ',
      booking_seat.seating_plan_seat
     ) as displayName
      from invoice
          join booking on booking.id = invoice.booking_id
          join booking_seat on booking_seat.booking_id = booking.id
          join event_showing on event_showing.id = booking.event_showing_id
          join event on event.id = event_showing.event_id
          join seating_plan on seating_plan.id = booking_seat.seating_plan_id
          join sector_price_event_showing
            on sector_price_event_showing.seating_plan_id = seating_plan.id
           and sector_price_event_showing.seating_plan_sector = booking_seat.seating_plan_sector
           and sector_price_event_showing.event_showing_id = event_showing.id
          join location on location.id = seating_plan.located_in
          join seating_plan_sector on seating_plan_sector.seating_plan_id = seating_plan.id
           and seating_plan_sector.number = booking_seat.seating_plan_sector
      where invoice.invoice_number = ?1
      """,
      nativeQuery = true)
  List<TicketSeatInfo> infoForSeatByInvoiceId(Long invoiceId);

  /**
   * Queries the TicketSeatInfo associated with the invoice.
   *
   * @param invoiceId to query for.
   * @return the TicketSeatInfo.
   */
  @Query(
      value =
          """
      select
      event.title as eventTitle,
      booking_non_seat.amount as quantity,
      location.name as location,
      seating_plan.name as room,
      seating_plan_sector.name as sector,
      seating_plan_sector.type,
      sector_price_event_showing.price as price,
     concat('Sector ', booking_non_seat.seating_plan_sector) as displayName
      from invoice
          join booking on booking.id = invoice.booking_id
          join booking_non_seat on booking_non_seat.booking_id = booking.id
          join event_showing on event_showing.id = booking.event_showing_id
          join event on event.id = event_showing.event_id
          join seating_plan on seating_plan.id = booking_non_seat.seating_plan_id
           and seating_plan.id = event_showing.performed_at
          join sector_price_event_showing
            on sector_price_event_showing.seating_plan_id = seating_plan.id
           and sector_price_event_showing.seating_plan_sector = booking_non_seat.seating_plan_sector
           and sector_price_event_showing.event_showing_id = event_showing.id
          join location on location.id = seating_plan.located_in
          join seating_plan_sector on seating_plan_sector.seating_plan_id = seating_plan.id
           and seating_plan_sector.number = booking_non_seat.seating_plan_sector
      where invoice.invoice_number = ?1
      """,
      nativeQuery = true)
  List<TicketSeatInfo> infoForNonSeatByInvoiceId(Long invoiceId);

  /**
   * Queries metadata for a given invoice.
   *
   * @param invoiceId to query metadata for.
   * @return the metadata.
   */
  @Query(
      value =
          """
            select event_showing.occurs_on as startingTime,
                    event.title as eventName,
                    location.name as locationName,
                    seating_plan.name as roomName,
                    booking.secret as secret
                    from invoice
                    join booking on invoice.booking_id = booking.id
                    join event_showing on booking.event_showing_id = event_showing.id
                    join event on event_showing.event_id = event.id
                    join seating_plan on seating_plan.id = event_showing.performed_at
                    join location on location.id = seating_plan.located_in
                   where invoice.invoice_number = ?1
            """,
      nativeQuery = true)
  Optional<TicketMetadata> metadataForInvoiceId(Long invoiceId);
}
