package at.ac.tuwien.sepm.groupphase.backend.service.invoice;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.InvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
  /**
   * Finds an invoice by its ID.
   *
   * @param id of the invoice to search for.
   * @return an Optional containing an invoice if it was found.
   */
  Optional<InvoiceDto> findById(Long id);

  /**
   * Finds all invoices associated with a given booking. Will contain anywhere from zero to two
   * invoices.
   *
   * @param bookingId of the booking to look for.
   * @return the list of invoices associated with a given booking.
   */
  List<Invoice> findInvoicesByBookingId(Long bookingId);
}
