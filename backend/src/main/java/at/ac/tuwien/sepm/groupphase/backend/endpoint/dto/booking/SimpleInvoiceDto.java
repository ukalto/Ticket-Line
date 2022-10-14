package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking;

import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceType;
import java.sql.Timestamp;

public record SimpleInvoiceDto(
    Long invoiceNumber, Timestamp invoiceDate, Long bookingId, InvoiceType invoiceType) {}
