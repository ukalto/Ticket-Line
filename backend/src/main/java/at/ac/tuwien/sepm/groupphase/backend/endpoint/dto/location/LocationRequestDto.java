package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record LocationRequestDto(
    @NotBlank(message = "Name must not be blank") @Pattern(regexp = "^[A-Za-zäÄöÖüÜß ]+$")
        String name,
    @NotBlank(message = "Country must not be blank") @Pattern(regexp = "^[A-Za-zäÄöÖüÜß ]+$")
        String country,
    @NotBlank(message = "Town must not be blank") @Pattern(regexp = "^[A-Za-zäÄöÖüÜß ]+$")
        String town,
    @NotBlank(message = "Street must not be blank") String street,
    @NotBlank(message = "Postal Code must not be blank") @Pattern(regexp = "^\\d{4,}$")
        String postalCode) {}
