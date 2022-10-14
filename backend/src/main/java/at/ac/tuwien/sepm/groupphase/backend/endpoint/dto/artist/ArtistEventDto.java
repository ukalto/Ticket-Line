package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventExpansionDetailsDto;

public record ArtistEventDto(String name, EventExpansionDetailsDto[] events) {}
