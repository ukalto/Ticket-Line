package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import java.math.BigDecimal;

public record EventDetailsShowsDto(
    Long id,
    Long eventId,
    String eventTitle,
    String date,
    String time,
    String location,
    String room,
    BigDecimal lowestPrice,
    BigDecimal highestPrice,
    boolean bookedOut) {}
