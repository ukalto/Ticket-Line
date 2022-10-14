package at.ac.tuwien.sepm.groupphase.backend.service.ticket;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketDto;
import java.util.List;

public interface TicketService {
  /**
   * Returns all tickets of a given invoice.
   *
   * @param invoiceId to find tickets for.
   * @return the tickets.
   */
  List<TicketDto> ticketsForInvoiceId(Long invoiceId);
}
