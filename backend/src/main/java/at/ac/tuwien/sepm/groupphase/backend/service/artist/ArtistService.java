package at.ac.tuwien.sepm.groupphase.backend.service.artist;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {
  /**
   * Creates an artist.
   *
   * @param artistRequestDto to be created
   * @return entity of created Artist
   */
  Artist create(ArtistRequestDto artistRequestDto);

  /**
   * Gets artistIds that contain a given string in any name.
   *
   * @param nameFilter string to be searched for
   * @return List of Artists that fulfill requirements
   */
  List<Artist> findArtistsByName(String nameFilter);

  /**
   * Filters artists with events by name filter.
   *
   * @param name name of the artists to be searched for
   * @param pageable defines pagination
   * @return List of Artists that fulfill requirements
   */
  Page<ArtistEventDto> filterArtistEvents(String name, Pageable pageable);
}
