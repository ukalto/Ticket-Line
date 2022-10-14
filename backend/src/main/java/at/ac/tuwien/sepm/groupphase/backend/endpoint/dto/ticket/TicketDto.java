package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record TicketDto(
    Timestamp startingTime,
    String eventName,
    String locationName,
    String roomName,
    BigDecimal price,
    String secret,
    String printableSeatDescription) {}
