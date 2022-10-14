package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

public record NewsEntryDetailsDto(
    Long showId, String title, String date, String contents, Long eventId, String file) {}
