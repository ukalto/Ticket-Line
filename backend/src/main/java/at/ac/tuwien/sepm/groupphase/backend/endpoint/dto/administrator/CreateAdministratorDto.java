package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.administrator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateAdministratorDto(
    @NotBlank @Email String email,
    @NotNull @Min(8) String password,
    @NotBlank ApplicationUserType type) {}
