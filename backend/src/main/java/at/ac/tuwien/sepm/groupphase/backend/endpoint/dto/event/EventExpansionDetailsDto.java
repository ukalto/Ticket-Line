package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

public record EventExpansionDetailsDto(
    Long id,
    String title,
    String description,
    String category,
    String imageReference,
    Boolean soldOut) {}
