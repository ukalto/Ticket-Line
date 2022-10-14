package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location;

public record LocationResponseDto(
    Long id, String name, String country, String town, String street, String postalCode) {}
