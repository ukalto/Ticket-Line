package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.customer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RegisterCustomerDto(
    @NotBlank(message = "Email must not be blank") @Email(message = "Email must be valid")
        String email,
    @NotBlank @Size(min = 8, message = "Password must be at least 8 digits") String password) {}
