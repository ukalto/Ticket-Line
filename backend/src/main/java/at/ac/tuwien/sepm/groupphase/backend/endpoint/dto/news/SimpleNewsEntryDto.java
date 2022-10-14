package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import java.sql.Timestamp;

public record SimpleNewsEntryDto(
    Long id,
    String title,
    String summary,
    Timestamp publishedOn,
    Long publishedBy,
    String imageRef) {}
