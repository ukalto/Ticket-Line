package at.ac.tuwien.sepm.groupphase.backend.unittests.service.impl;

import static org.mockito.Mockito.*;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventExpansionDetailsMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventExpansionsDetailRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.location.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.location.impl.LocationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class LocationServiceImplTest {

  private LocationService locationService;
  private LocationRepository locationRepository;
  private EventExpansionsDetailRepository eventExpansionsDetailRepository;
  private EventExpansionDetailsMapper eventExpansionDetailsMapper;

  @BeforeEach
  void setup() {
    this.locationRepository = mock(LocationRepository.class);
    this.eventExpansionsDetailRepository = mock(EventExpansionsDetailRepository.class);
    this.eventExpansionDetailsMapper = mock(EventExpansionDetailsMapper.class);
    this.locationService =
        new LocationServiceImpl(
            locationRepository,
            new LocationMapper(),
            eventExpansionsDetailRepository,
            eventExpansionDetailsMapper);
  }

  @Test
  void createLocationWithExistingName_shouldThrow() {
    var locationRequestDto =
        new LocationRequestDto(
            "Cineplex Wienerberg", "Austria", "Vienna", "Gießhübelstraße 12", "1100");

    when(locationRepository.existsByName(locationRequestDto.name())).thenReturn(true);

    Assertions.assertThrows(
        AlreadyExistsException.class, () -> this.locationService.save(locationRequestDto));
  }

  @Test
  void createLocationWithUniqueName_shouldWork() {
    LocationRequestDto locationRequestDto =
        new LocationRequestDto(
            "Cineplex Wienerberg", "Austria", "Vienna", "Gießhübelstraße 12", "1100");

    when(locationRepository.existsByName(locationRequestDto.name())).thenReturn(false);

    var captor = ArgumentCaptor.forClass(Location.class);
    this.locationService.save(locationRequestDto);
    verify(locationRepository).save(captor.capture());
    var createdLocation = captor.getValue();

    Assertions.assertEquals(locationRequestDto.name(), createdLocation.getName());
    Assertions.assertEquals(locationRequestDto.country(), createdLocation.getCountry());
    Assertions.assertEquals(locationRequestDto.town(), createdLocation.getTown());
    Assertions.assertEquals(locationRequestDto.street(), createdLocation.getStreet());
    Assertions.assertEquals(locationRequestDto.postalCode(), createdLocation.getPostalCode());
  }
}
