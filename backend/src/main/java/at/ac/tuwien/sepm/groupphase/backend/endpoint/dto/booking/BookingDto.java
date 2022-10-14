package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record BookingDto(
    Long bookingId,
    Long bookedBy,
    Timestamp bookedAt,
    Long eventShowingId,
    boolean isCancelled,
    BigDecimal cost) {}
