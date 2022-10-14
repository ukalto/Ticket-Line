package at.ac.tuwien.sepm.groupphase.backend.service.location.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventExpansionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventExpansionDetailsMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventExpansionsDetailRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.location.LocationService;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LocationServiceImpl implements LocationService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;
  private final EventExpansionsDetailRepository eventExpansionsDetailRepository;
  private final EventExpansionDetailsMapper eventExpansionDetailsMapper;

  @Override
  public Location save(LocationRequestDto locationRequestDto) {
    if (locationRepository.existsByName(locationRequestDto.name())) {
      throw new AlreadyExistsException(
          "Location Name '" + locationRequestDto.name() + "' is already taken!");
    }
    return locationRepository.save(locationMapper.requestDtoToEntity(locationRequestDto));
  }

  @Override
  public List<Location> findAll() {
    return locationRepository.findAllByOrderByNameDesc();
  }

  @Override
  public List<Location> find(LocationRequestDto locationRequestDto, Integer limit) {
    LOGGER.debug(
        "Searching locations matching the search criteria, results limited to {} results", limit);

    Pageable pageable = PageRequest.of(0, limit);

    LocationRequestDto normalizedLocationDto = normalize(locationRequestDto);

    return locationRepository
        .findByNameContainingAndCountryContainingAndTownContainingAndStreetContainingAndPostalCodeContainingAllIgnoreCase(
            normalizedLocationDto.name(),
            normalizedLocationDto.country(),
            normalizedLocationDto.town(),
            normalizedLocationDto.street(),
            normalizedLocationDto.postalCode(),
            pageable)
        .getContent();
  }

  @Override
  public Page<LocationEventDto> filterLocationEvents(
      String name,
      String country,
      String town,
      String street,
      String postalCode,
      Pageable pageable) {
    var locations =
        this.locationRepository
            .findByNameContainingAndCountryContainingAndTownContainingAndStreetContainingAndPostalCodeContainingAllIgnoreCase(
                name, country, town, street, postalCode, pageable);

    List<LocationEventDto> locationEventDtoList = new ArrayList<>();

    locations.forEach(
        location -> {
          var eventsResponse =
              this.eventExpansionsDetailRepository.findEventsForLocation(location.getId());

          var mappedEvents =
              eventsResponse.stream()
                  .map(eventExpansionDetailsMapper::eventExpansionDetailsToEventExpansionDetailsDto)
                  .toArray(EventExpansionDetailsDto[]::new);
          locationEventDtoList.add(
              new LocationEventDto(
                  location.getName(),
                  location.getCountry(),
                  location.getTown(),
                  location.getStreet(),
                  location.getPostalCode(),
                  mappedEvents));
        });
    locationEventDtoList.sort((o1, o2) -> Integer.compare(o2.events().length, o1.events().length));
    return new PageImpl<LocationEventDto>(
        locationEventDtoList, pageable, locations.getTotalElements());
  }

  private LocationRequestDto normalize(LocationRequestDto locationRequestDto) {
    String normalizedName = locationRequestDto.name() == null ? "" : locationRequestDto.name();
    String normalizedCountry =
        locationRequestDto.country() == null ? "" : locationRequestDto.country();
    String normalizedTown = locationRequestDto.town() == null ? "" : locationRequestDto.town();
    String normalizedStreet =
        locationRequestDto.street() == null ? "" : locationRequestDto.street();
    String normalizedPostalCode =
        locationRequestDto.postalCode() == null ? "" : locationRequestDto.postalCode();

    return new LocationRequestDto(
        normalizedName, normalizedCountry, normalizedTown, normalizedStreet, normalizedPostalCode);
  }
}
