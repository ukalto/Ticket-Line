package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import javax.validation.constraints.Email;

public record EditUserAccessRequestDto(
    @Email(message = "must be an email!") String email, String currentPassword, String password) {}
