package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record NewsEntryCreationDto(
    @NotBlank(message = "Title must not be blank") String title,
    @NotBlank(message = "Contents must not be blank") String contents,
    String summary,
    @NotNull(message = "Publisher ID must not be null") Long publishedBy,
    String imageRef,
    @NotNull(message = "Event ID must not be null") Long eventId) {}
