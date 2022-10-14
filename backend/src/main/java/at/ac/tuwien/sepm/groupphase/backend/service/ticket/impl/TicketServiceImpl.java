package at.ac.tuwien.sepm.groupphase.backend.service.ticket.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ticket.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ticket.TicketService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketServiceImpl implements TicketService {
  private final TicketRepository ticketRepository;

  public TicketServiceImpl(final TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  @Override
  @Transactional
  public List<TicketDto> ticketsForInvoiceId(Long invoiceId) {
    final var maybeMetadata = this.ticketRepository.metadataForInvoiceId(invoiceId);
    if (maybeMetadata.isEmpty()) {
      throw new NotFoundException("Invoice could not be found");
    }

    final var seatInformation = this.ticketRepository.infoForSeatByInvoiceId(invoiceId);
    final var nonSeatInformation = this.ticketRepository.infoForNonSeatByInvoiceId(invoiceId);

    final var mergedInfos =
        Stream.concat(seatInformation.stream(), nonSeatInformation.stream()).toList();

    final var metadata = maybeMetadata.get();
    final var tickets = new ArrayList<TicketDto>();

    for (final var info : mergedInfos) {
      final var quantity = info.getQuantity().longValue();
      for (var i = 0; i < quantity; i++) {
        final var mappedTicket =
            new TicketDto(
                metadata.getStartingTime(),
                metadata.getEventName(),
                metadata.getLocationName(),
                metadata.getRoomName(),
                info.getPrice(),
                metadata.getSecret(),
                info.getDisplayName());

        tickets.add(mappedTicket);
      }
    }

    return tickets;
  }
}
