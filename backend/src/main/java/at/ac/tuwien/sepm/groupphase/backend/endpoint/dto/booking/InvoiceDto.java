package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking;

import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record InvoiceDto(
    Long invoiceNumber,
    String companyLocation,
    String taxNumber,
    Timestamp invoiceDate,
    Long bookingId,
    InvoiceType invoiceType,
    Long cancelledInvoiceId,
    List<InvoiceLine> lines,
    BigDecimal netSum,
    BigDecimal vatAmount,
    BigDecimal grossSum,
    String footerText) {

  public record InvoiceLine(
      String eventTitle,
      Integer quantity,
      String location,
      String room,
      String sector,
      String type,
      BigDecimal pricePerUnit) {}
}
