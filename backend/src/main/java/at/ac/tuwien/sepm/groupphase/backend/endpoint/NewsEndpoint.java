package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsEntryMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.news.NewsEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class NewsEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final NewsEntryService newsEntryService;
  private final NewsEntryMapper newsEntryMapper;
  private final ImageService imageService;

  @Secured("ROLE_ADMINISTRATOR")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/news-entry")
  @Operation(summary = "Creates a news entry", security = @SecurityRequirement(name = "apiKey"))
  public DetailedNewsEntryDto create(@Valid @RequestBody NewsEntryCreationDto newsEntry) {
    LOGGER.info("POST /api/v1/news-entry body: {}", newsEntry);
    return newsEntryMapper.newsEntryToDetailedNewsEntryDto(
        newsEntryService.publishNewsEntry(newsEntry));
  }

  @PermitAll()
  @GetMapping("/news-entry/{id}")
  @Operation(summary = "List a news entry")
  public NewsEntryDetailsDto getNewsEntryDetails(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/news-entry/{}", id);
    return newsEntryService.findNewsEntryDetailsById(id);
  }

  @PermitAll
  @GetMapping({"/news", "/news/{userId}"})
  @Operation(summary = "Lists news entrys matching the search criteria")
  public List<NewsEntryOverviewDto> getNews(@PathVariable(required = false) Long userId) {
    LOGGER.info("GET /api/v1/news/{}", userId);
    return newsEntryService.findAll(userId);
  }

  @PermitAll
  @GetMapping("/news/{userId}/archive")
  @Operation(summary = "Lists news entrys matching the search criteria")
  public List<NewsEntryOverviewDto> getNewsRead(@PathVariable Long userId) {
    LOGGER.info("GET /api/v1/news/{}/archive", userId);
    return newsEntryService.findAllRead(userId);
  }

  @Secured("ROLE_ADMINISTRATOR")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/news-entry/{id}/image")
  @Operation(summary = "Saves an image to the related news entry")
  public void handleFileUpload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    LOGGER.info("POST /api/v1/news-entry/{}/image", id);
    imageService.store(file, "n_" + id);
  }

  @Secured({"ROLE_USER", "ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMIN"})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/news-entry/{newsId}/viewing/{userId}")
  @Operation(summary = "Saves an image to the related news entry")
  public NewsEntryReadByDto registerNewsRead(@PathVariable Long newsId, @PathVariable Long userId) {
    LOGGER.info("POST /api/v1/news-entry/{}/viewing/{}", newsId, userId);
    return newsEntryMapper.newsEntryReadByToNewsEntryReadByDto(
        newsEntryService.registerNewsEntryViewing(newsId, userId));
  }
}
