package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import java.sql.Timestamp;

public record ShowingDto(Long id, Timestamp occursOn, Long performedAt, Long eventId) {}
