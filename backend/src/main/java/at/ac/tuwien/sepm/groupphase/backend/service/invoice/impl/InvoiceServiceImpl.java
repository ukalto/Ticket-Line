package at.ac.tuwien.sepm.groupphase.backend.service.invoice.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking.InvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.booking.printinvoice.PrintInvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.invoice.InvoiceService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {
  private static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.2);

  private final PrintInvoiceRepository repository;
  private final InvoiceRepository invoiceRepository;

  public InvoiceServiceImpl(
      final PrintInvoiceRepository repository, final InvoiceRepository invoiceRepository) {
    this.repository = repository;
    this.invoiceRepository = invoiceRepository;
  }

  @Override
  public Optional<InvoiceDto> findById(Long id) {
    var maybeInvoice = this.repository.findOneById(id);
    var maybeLines = this.repository.findLinesByInvoiceId(id);

    if (maybeInvoice.isEmpty() || maybeLines.isEmpty()) {
      throw new NotFoundException("Could not find invoice.");
    }

    var invoice = maybeInvoice.get();
    var lines = maybeLines.get();

    var linesDto =
        lines.stream()
            .map(
                line ->
                    new InvoiceDto.InvoiceLine(
                        line.getEventTitle(),
                        line.getQuantity(),
                        line.getLocation(),
                        line.getRoom(),
                        line.getSector(),
                        line.getType(),
                        line.getPricePerUnit()))
            .collect(Collectors.toList());

    var grossSum =
        linesDto.stream()
            .map(
                line ->
                    line.pricePerUnit().multiply(BigDecimal.valueOf(line.quantity().longValue())))
            .reduce(new BigDecimal(0), BigDecimal::add);

    var netSum =
        grossSum
            .divide(BigDecimal.valueOf(1).add(VAT_RATE), RoundingMode.HALF_EVEN)
            .multiply(BigDecimal.valueOf(1).subtract(VAT_RATE));

    var vatAmount = grossSum.subtract(netSum);

    var localDate = invoice.getOccursOn().toLocalDateTime();
    var startHour = localDate.getHour();
    var startMinutes = localDate.getMinute();
    var startDate =
        String.format(
            "%02d%n.%02d%n.%02d%n ",
            localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
    var footerLine =
        String.format(
            "Performance date / time: %s - %02d%n:%02d%n", startDate, startHour, startMinutes);

    var invoiceDto =
        new InvoiceDto(
            invoice.getInvoiceNumber(),
            invoice.getCompanyLocation(),
            "ATU12345677",
            invoice.getInvoiceDate(),
            invoice.getBookingId(),
            InvoiceType.convertToEnum(invoice.getInvoiceType()),
            invoice.getCancelledInvoiceId(),
            linesDto,
            netSum,
            vatAmount,
            grossSum,
            footerLine);

    return Optional.of(invoiceDto);
  }

  @Override
  public List<Invoice> findInvoicesByBookingId(Long bookingId) {
    return this.invoiceRepository.findAllByBookingId(bookingId);
  }
}
