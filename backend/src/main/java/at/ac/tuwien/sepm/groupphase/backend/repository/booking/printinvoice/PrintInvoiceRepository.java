package at.ac.tuwien.sepm.groupphase.backend.repository.booking.printinvoice;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintInvoiceRepository extends JpaRepository<Invoice, Long> {
  /**
   * Retrieves an invoice by its invoice number.
   *
   * @param invoiceNumber to search for.
   * @return the queried PrintInvoice.
   */
  @Query(
      value =
          """
      select
        invoice.invoice_number as invoiceNumber,
        concat(location.street, ', ', location.postal_code, ' ', location.town) as companyLocation,
        invoice.purchased_at as invoiceDate,
        invoice.booking_id as bookingId,
        invoice.invoice_type as invoiceType,
        (
          select cancellation_invoice.invoice_number
            from invoice as cancellation_invoice
            join invoice as purchase_invoice on cancellation_invoice.booking_id = purchase_invoice.booking_id
           where purchase_invoice.invoice_type = 'PURCHASE' and cancellation_invoice.invoice_type = 'CANCELLATION'
             and cancellation_invoice.booking_id = invoice.booking_id
        ) as cancelledInvoiceId,
        event_showing.occurs_on as occursOn,
        event.duration as duration,
        from invoice
        join booking on booking.id = invoice.booking_id
        join event_showing on event_showing.id = booking.event_showing_id
        join seating_plan on seating_plan.id = event_showing.performed_at
        join location on location.id = seating_plan.located_in
        join event on event.id = event_showing.event_id
       where invoice.invoice_number = ?1
      """,
      nativeQuery = true)
  Optional<PrintInvoice> findOneById(Long invoiceNumber);

  /**
   * Finds all invoice lines of a given invoice.
   *
   * @param invoiceNumber of the associated invoice lines.
   * @return the invoice lines.
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
      seating_plan_sector.type as type,
      sector_price_event_showing.price as pricePerUnit
      from invoice
          join booking on booking.id = invoice.booking_id
          join booking_non_seat on booking_non_seat.booking_id = booking.id
          join event_showing on event_showing.id = booking.event_showing_id
          join event on event.id = event_showing.event_id
          join seating_plan on seating_plan.id = booking_non_seat.seating_plan_id
            and seating_plan.id = event_showing.performed_at
          join sector_price_event_showing on sector_price_event_showing.seating_plan_id = seating_plan.id
           and sector_price_event_showing.seating_plan_sector = booking_non_seat.seating_plan_sector
           and sector_price_event_showing.event_showing_id = event_showing.id
          join location on location.id = seating_plan.located_in
          join seating_plan_sector on seating_plan_sector.seating_plan_id = seating_plan.id
           and seating_plan_sector.number = booking_non_seat.seating_plan_sector
         where invoice.invoice_number = ?1

      union all

      select
      event.title as eventTitle,
      1 as quantity,
      location.name as location,
      seating_plan.name as room,
      seating_plan_sector.name as sector,
      seating_plan_sector.type as type,
      sector_price_event_showing.price as pricePerUnit
      from invoice
          join booking on booking.id = invoice.booking_id
          join booking_seat on booking_seat.booking_id = booking.id
          join event_showing on event_showing.id = booking.event_showing_id
          join event on event.id = event_showing.event_id
          join seating_plan on seating_plan.id = booking_seat.seating_plan_id
           and seating_plan.id = event_showing.performed_at
          join sector_price_event_showing on sector_price_event_showing.seating_plan_id = seating_plan.id
           and sector_price_event_showing.seating_plan_sector = booking_seat.seating_plan_sector
           and sector_price_event_showing.event_showing_id = event_showing.id
          join location on location.id = seating_plan.located_in
          join seating_plan_sector on seating_plan_sector.seating_plan_id = seating_plan.id
           and seating_plan_sector.number = booking_seat.seating_plan_sector
      where invoice.invoice_number = ?1
      """,
      nativeQuery = true)
  Optional<List<InvoiceLine>> findLinesByInvoiceId(Long invoiceNumber);
}
