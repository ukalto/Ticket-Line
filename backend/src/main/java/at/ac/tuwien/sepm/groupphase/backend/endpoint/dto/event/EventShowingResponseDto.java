package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import java.sql.Timestamp;

public record EventShowingResponseDto(
    Long id, Timestamp occursOn, Long performedAt, Long eventId) {}
