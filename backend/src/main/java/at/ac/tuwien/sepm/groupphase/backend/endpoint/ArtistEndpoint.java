package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.artist.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
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
public class ArtistEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ArtistService artistService;
  private final ArtistMapper artistMapper;

  @Secured("ROLE_ADMINISTRATOR")
  @PostMapping("/artist")
  @Operation(summary = "Creates an artist", security = @SecurityRequirement(name = "apiKey"))
  public ResponseEntity<?> create(@RequestBody ArtistRequestDto artistRequestDto) {
    LOGGER.info("POST /api/v1/artist created: {}", artistRequestDto);
    return ResponseEntity.ok(
        artistMapper.entityToResponseDto(artistService.create(artistRequestDto)));
  }

  @Secured("ROLE_ADMINISTRATOR")
  @GetMapping("/artists")
  @Operation(
      summary = "Lists artistIds matching the search criteria",
      security = @SecurityRequirement(name = "apiKey"))
  public List<ArtistResponseDto> getArtists(@RequestParam(required = false) String nameFilter) {
    LOGGER.info("GET api/v1/artistIds with name filter: {}", nameFilter);
    if (nameFilter == null) {
      nameFilter = "";
    }
    return artistService.findArtistsByName(nameFilter).stream()
        .map(artistMapper::entityToResponseDto)
        .collect(Collectors.toList());
  }

  @GetMapping("/artists-events")
  @Operation(
      summary = "Returns matching artists with events",
      security = @SecurityRequirement(name = "apiKey"))
  public Page<ArtistEventDto> getArtistsWithEvents(
      @RequestParam(required = false, defaultValue = "") String name,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    LOGGER.info("GET api/v1/artists-events with name: {}", name);
    return artistService.filterArtistEvents(name, PageRequest.of(page, size));
  }
}
