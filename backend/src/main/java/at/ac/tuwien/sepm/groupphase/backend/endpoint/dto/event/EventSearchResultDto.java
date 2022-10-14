package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

public record EventSearchResultDto(
    Long id,
    String title,
    String description,
    Long categoryId,
    Boolean soldOut,
    Boolean hasShowingsInFuture,
    String imageFile,
    Long durationHours,
    int durationMinutes) {}
