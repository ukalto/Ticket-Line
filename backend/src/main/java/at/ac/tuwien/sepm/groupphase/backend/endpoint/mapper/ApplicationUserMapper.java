package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUserMapper {

  public ApplicationUserDto entityToResponseDto(ApplicationUser applicationUser) {
    return new ApplicationUserDto(
        applicationUser.getId(),
        applicationUser.getEmail(),
        applicationUser.isBlocked(),
        applicationUser.getType());
  }
}
