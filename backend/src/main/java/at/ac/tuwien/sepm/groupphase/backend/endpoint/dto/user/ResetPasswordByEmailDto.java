package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record ResetPasswordByEmailDto(@Email @NotBlank String email) {}
