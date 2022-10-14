package at.ac.tuwien.sepm.groupphase.backend.repository.booking.printinvoice;

import java.math.BigInteger;
import java.sql.Timestamp;

public interface PrintInvoice {
  Long getInvoiceNumber();

  String getCompanyLocation();

  Timestamp getInvoiceDate();

  Long getBookingId();

  String getInvoiceType();

  Long getCancelledInvoiceId();

  Timestamp getOccursOn();

  BigInteger getDuration();
}
