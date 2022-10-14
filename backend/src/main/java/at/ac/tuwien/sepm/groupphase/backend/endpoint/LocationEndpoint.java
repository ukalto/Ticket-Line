package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SeatingPlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;
import at.ac.tuwien.sepm.groupphase.backend.service.location.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.SeatingPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LocationEndpoint {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final LocationService locationService;
  private final LocationMapper locationMapper;
  private final SeatingPlanService seatingPlanService;
  private final SeatingPlanMapper seatingPlanMapper;

  @Secured("ROLE_ADMINISTRATOR")
  @PostMapping("/location")
  @Operation(summary = "Creates a new location", security = @SecurityRequirement(name = "apiKey"))
  public ResponseEntity<?> createLocation(
      @Valid @RequestBody LocationRequestDto locationRequestDto) {
    LOGGER.info("Creating location with name {}", locationRequestDto.name());
    return ResponseEntity.ok(
        locationMapper.entityToResponseDto(locationService.save(locationRequestDto)));
  }

  @GetMapping("/locations")
  @Operation(
      summary = "Lists locations matching the search criteria",
      security = @SecurityRequirement(name = "apiKey"))
  public Stream<LocationResponseDto> getLocations(
      LocationRequestDto locationRequestDto, Integer limit) {
    LOGGER.info("Finding locations");
    return locationService.find(locationRequestDto, limit).stream()
        .map(locationMapper::entityToResponseDto);
  }

  @Secured("ROLE_ADMINISTRATOR")
  @PostMapping("/location/{id}/seating-plan")
  @Operation(
      summary = "Creates a new seating plan in a location",
      security = @SecurityRequirement(name = "apiKey"))
  public SeatingPlanDto createSeatingPlan(
      @Valid @RequestBody SeatingPlanCreationDto seatingPlanCreationDto) {
    LOGGER.info("Creating seating plan with name {}", seatingPlanCreationDto.name());
    return seatingPlanMapper.entityToDto(seatingPlanService.save(seatingPlanCreationDto));
  }

  @GetMapping("/location/seating-plan/{seatingPlanId}")
  @Operation(summary = "Lists one seating plan", security = @SecurityRequirement(name = "apiKey"))
  public SeatingPlanResponseDto getOneSeatingPlan(@PathVariable Long seatingPlanId) {
    LOGGER.info("GET /api/v1/location/seating-plan/{}", seatingPlanId);
    return seatingPlanService.findSeatingPlanById(seatingPlanId);
  }

  @GetMapping("/location/{id}/seating-plans")
  @Operation(
      summary = "Lists seating plans of one location matching the search criteria",
      security = @SecurityRequirement(name = "apiKey"))
  public List<SeatingPlan> getSeatingPlans(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/location/{}/seating-plans", id);
    return seatingPlanService.findSeatingPlansByLocation(id);
  }

  @GetMapping("/locations-events")
  @Operation(
      summary = "Returns locations with events",
      security = @SecurityRequirement(name = "apiKey"))
  public Page<LocationEventDto> getLocationsWithEvents(
      @RequestParam(required = false, defaultValue = "") String name,
      @RequestParam(required = false, defaultValue = "") String street,
      @RequestParam(required = false, defaultValue = "") String country,
      @RequestParam(required = false, defaultValue = "") String town,
      @RequestParam(required = false, defaultValue = "") String postalCode,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    LOGGER.info(
        "GET /api/v1/locations-events filtered with {} {} {} {} {}",
        name,
        street,
        country,
        town,
        postalCode);
    return locationService.filterLocationEvents(
        name, country, town, street, postalCode, PageRequest.of(page, size));
  }
}
