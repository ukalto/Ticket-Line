package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventExpansionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.custom.EventExpansionDetails;
import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import org.springframework.stereotype.Component;

@Component
public class EventExpansionDetailsMapper {
  private final ImageService imageService;

  public EventExpansionDetailsMapper(ImageService imageService) {
    this.imageService = imageService;
  }

  public EventExpansionDetailsDto eventExpansionDetailsToEventExpansionDetailsDto(
      EventExpansionDetails e) {
    return new EventExpansionDetailsDto(
        e.getId(),
        e.getTitle(),
        e.getDescription(),
        e.getCategory(),
        imageService.imageToBase64(e.getImageReference()),
        e.getSoldOut());
  }
}
