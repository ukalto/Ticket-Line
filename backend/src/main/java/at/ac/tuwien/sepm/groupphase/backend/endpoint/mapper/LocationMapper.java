package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

  public Location requestDtoToEntity(LocationRequestDto locationRequestDto) {
    Location locationEntity = new Location();

    locationEntity.setName(locationRequestDto.name());
    locationEntity.setCountry(locationRequestDto.country());
    locationEntity.setTown(locationRequestDto.town());
    locationEntity.setStreet(locationRequestDto.street());
    locationEntity.setPostalCode(locationRequestDto.postalCode());

    return locationEntity;
  }

  public LocationResponseDto entityToResponseDto(Location location) {
    return new LocationResponseDto(
        location.getId(),
        location.getName(),
        location.getCountry(),
        location.getTown(),
        location.getStreet(),
        location.getPostalCode());
  }
}
