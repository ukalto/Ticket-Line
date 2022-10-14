package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import java.time.Duration;

public record TopEventResponseDto(
    Long id,
    String title,
    Long categoryId,
    Duration duration,
    String description,
    Long totalBookings) {}
