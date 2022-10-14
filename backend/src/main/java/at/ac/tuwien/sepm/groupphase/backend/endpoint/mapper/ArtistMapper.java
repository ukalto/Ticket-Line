package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {
  public Artist requestDtoToEntity(ArtistRequestDto artistRequestDto) {
    Artist artist = new Artist();
    artist.setFirstName(artistRequestDto.firstName());
    artist.setLastName(artistRequestDto.lastName());
    artist.setArtistName(artistRequestDto.artistName());
    return artist;
  }

  public ArtistResponseDto entityToResponseDto(Artist artist) {
    return new ArtistResponseDto(
        artist.getId(), artist.getFirstName(), artist.getLastName(), artist.getArtistName());
  }
}
