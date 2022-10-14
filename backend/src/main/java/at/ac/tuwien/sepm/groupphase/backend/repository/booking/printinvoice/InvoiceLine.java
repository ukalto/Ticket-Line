package at.ac.tuwien.sepm.groupphase.backend.repository.booking.printinvoice;

import java.math.BigDecimal;

public interface InvoiceLine {
  String getEventTitle();

  Integer getQuantity();

  String getLocation();

  String getRoom();

  String getSector();

  String getType();

  BigDecimal getPricePerUnit();
}
