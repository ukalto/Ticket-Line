package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

public record EventDto(Long id, String title, Long categoryId, String description, String file) {}
