package at.ac.tuwien.sepm.groupphase.backend.repository.ticket;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface TicketSeatInfo {
  BigDecimal getPrice();

  String getDisplayName();

  BigInteger getQuantity();
}
