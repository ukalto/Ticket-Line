package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventExpansionDetailsDto;

public record LocationEventDto(
    String name,
    String country,
    String town,
    String street,
    String postalCode,
    EventExpansionDetailsDto[] events) {}
