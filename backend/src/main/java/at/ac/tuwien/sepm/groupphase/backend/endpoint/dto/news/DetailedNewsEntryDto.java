package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import java.sql.Timestamp;

public record DetailedNewsEntryDto(
    Long id,
    String title,
    String contents,
    String summary,
    Timestamp publishedOn,
    Long publishedBy,
    String imageRef,
    Long eventId) {}
