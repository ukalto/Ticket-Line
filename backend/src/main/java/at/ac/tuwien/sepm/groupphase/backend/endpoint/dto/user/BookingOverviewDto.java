package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import at.ac.tuwien.sepm.groupphase.backend.entity.BookingState;
import java.math.BigDecimal;

public record BookingOverviewDto(
    Long bookingId,
    Long invoiceNumber,
    Long cancellationInvoiceNumber,
    String file,
    String title,
    String date,
    String duration,
    int ticketCount,
    BigDecimal price,
    BookingState bookingState) {}
