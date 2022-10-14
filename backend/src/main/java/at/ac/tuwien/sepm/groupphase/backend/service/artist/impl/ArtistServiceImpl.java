package at.ac.tuwien.sepm.groupphase.backend.service.artist.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventExpansionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventExpansionDetailsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventExpansionsDetailRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.artist.ArtistService;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ArtistServiceImpl implements ArtistService {

  private final ArtistRepository artistRepository;
  private final EventExpansionsDetailRepository eventExpansionsDetailRepository;
  private final EventExpansionDetailsMapper eventExpansionDetailsMapper;
  private final ArtistMapper artistMapper;

  @Override
  public Artist create(ArtistRequestDto artistRequestDto) {
    if (artistRequestDto.artistName().isBlank()
        && (!artistRequestDto.firstName().matches("^[A-Za-zäÄöÖüÜß]+$")
            || !artistRequestDto.lastName().matches("^[A-Za-zäÄöÖüÜß]+$"))) {
      throw new ValidationException("Artist is invalid!");
    }
    return artistRepository.save(artistMapper.requestDtoToEntity(artistRequestDto));
  }

  public List<Artist> findArtistsByName(String nameFilter) {
    return artistRepository
        .findTop10ByArtistNameContainingOrFirstNameContainingOrLastNameContainingAllIgnoreCase(
            nameFilter, nameFilter, nameFilter);
  }

  @Override
  public Page<ArtistEventDto> filterArtistEvents(String name, Pageable pageable) {
    var artists = artistRepository.findAllByNameOrAlias(name, pageable);
    List<ArtistEventDto> result = new ArrayList<>();
    artists.forEach(
        artist -> {
          var events =
              eventExpansionsDetailRepository.findEventsForArtist(artist.getId()).stream()
                  .map(eventExpansionDetailsMapper::eventExpansionDetailsToEventExpansionDetailsDto)
                  .toArray(EventExpansionDetailsDto[]::new);
          result.add(new ArtistEventDto(artist.getArtistName(), events));
        });
    result.sort((o1, o2) -> Integer.compare(o2.events().length, o1.events().length));
    return new PageImpl<ArtistEventDto>(result, pageable, artists.getTotalElements());
  }
}
