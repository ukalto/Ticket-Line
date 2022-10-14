package at.ac.tuwien.sepm.groupphase.backend.repository.ticket;

import java.sql.Timestamp;

public interface TicketMetadata {
  Timestamp getStartingTime();

  String getEventName();

  String getLocationName();

  String getRoomName();

  String getSecret();
}
