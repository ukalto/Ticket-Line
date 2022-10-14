package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.SimpleInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

  public BookingDto bookingEntityToBookingDto(Booking booking) {
    return new BookingDto(
        booking.getId(),
        booking.getBookedBy(),
        booking.getBookedAt(),
        booking.getEventShowingId(),
        booking.isCancelled(),
        booking.getCost());
  }

  public SimpleInvoiceDto invoiceEntityToInvoiceDto(Invoice invoice) {
    return new SimpleInvoiceDto(
        invoice.getInvoiceNumber(),
        invoice.getPurchasedAt(),
        invoice.getBookingId(),
        invoice.getInvoiceType());
  }
}
