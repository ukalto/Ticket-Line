package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record ArtistRequestDto(
    @NotBlank(message = "First Name must not be blank") @Pattern(regexp = "^[A-Za-zäÄöÖüÜß]+$")
        String firstName,
    @NotBlank(message = "Last Name must not be blank") @Pattern(regexp = "^[A-Za-zäÄöÖüÜß]+$")
        String lastName,
    @NotBlank(message = "Artist Name must not be blank") String artistName) {}
