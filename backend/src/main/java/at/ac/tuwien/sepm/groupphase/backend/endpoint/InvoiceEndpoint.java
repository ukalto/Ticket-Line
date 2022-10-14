package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.InvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.SimpleInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.BookingOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.BookingMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.booking.BookingService;
import at.ac.tuwien.sepm.groupphase.backend.service.invoice.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InvoiceEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final InvoiceService invoiceService;
  private final BookingService bookingService;
  private final BookingMapper bookingMapper;
  private final TicketService ticketService;

  @Secured("ROLE_USER")
  @GetMapping("/invoice/{id}")
  @Operation(
      summary = "Return invoice of ticket matching ID",
      security = @SecurityRequirement(name = "apiKey"))
  public Optional<InvoiceDto> getInvoice(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/invoice/{}", id);
    return this.invoiceService.findById(id);
  }

  @Secured("ROLE_USER")
  @GetMapping("/booking/{bookingId}/invoices")
  @Operation(
      summary = "Return all invoices of a booking",
      security = @SecurityRequirement(name = "apiKey"))
  public List<SimpleInvoiceDto> getInvoicesByBooking(@PathVariable Long bookingId) {
    LOGGER.info("GET /api/v1/booking/{}/invoices", bookingId);
    return this.invoiceService.findInvoicesByBookingId(bookingId).stream()
        .map(this.bookingMapper::invoiceEntityToInvoiceDto)
        .collect(Collectors.toList());
  }

  @Secured("ROLE_USER")
  @GetMapping("/tickets/{invoiceId}")
  @Operation(
      summary = "Lists all tickets of a user",
      security = @SecurityRequirement(name = "apiKey"))
  public List<TicketDto> getAll(@PathVariable Long invoiceId) {
    LOGGER.info("GET /api/v1/tickets/{}", invoiceId);
    return this.ticketService.ticketsForInvoiceId(invoiceId);
  }

  @Secured("ROLE_USER")
  @PatchMapping("/booking/{bookingId}/cancellation")
  @Operation(summary = "Post cancelled booking", security = @SecurityRequirement(name = "apiKey"))
  public BookingOverviewDto cancelBooking(@PathVariable Long bookingId) {
    LOGGER.info("PATCH /api/v1/booking/{}/cancellation", bookingId);
    return bookingService.cancelBooking(bookingId);
  }
}
