package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;

public record ApplicationUserDto(
    Long id, String email, boolean isBlocked, ApplicationUserType type) {}
