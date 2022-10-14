package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

public record EventResponseDto(
    Long id, String title, Long categoryId, String description, Long performedBy) {}
