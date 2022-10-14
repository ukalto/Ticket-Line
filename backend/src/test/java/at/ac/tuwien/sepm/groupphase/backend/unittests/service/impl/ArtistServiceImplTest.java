package at.ac.tuwien.sepm.groupphase.backend.unittests.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventExpansionDetailsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.event.EventExpansionsDetailRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.artist.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.service.artist.impl.ArtistServiceImpl;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ArtistServiceImplTest {
  private ArtistService artistService;
  private ArtistRepository artistRepository;
  private EventExpansionsDetailRepository eventExpansionsDetailRepository;
  private EventExpansionDetailsMapper eventExpansionDetailsMapper;

  @BeforeEach
  void setup() {
    this.artistRepository = mock(ArtistRepository.class);
    this.eventExpansionsDetailRepository = mock(EventExpansionsDetailRepository.class);
    this.eventExpansionDetailsMapper = mock(EventExpansionDetailsMapper.class);
    this.artistService =
        new ArtistServiceImpl(
            artistRepository,
            eventExpansionsDetailRepository,
            eventExpansionDetailsMapper,
            new ArtistMapper());
  }

  @Test
  void createArtistWithNoValues_shouldThrow() {
    var artistRequestDto = new ArtistRequestDto("", "", "");

    Assertions.assertThrows(
        ValidationException.class, () -> this.artistService.create(artistRequestDto));
  }

  @Test
  void createArtistWithCorrectValues_shouldWork() {
    var artistRequestDto = new ArtistRequestDto("Peter", "Mafai", "PM");

    var captor = ArgumentCaptor.forClass(Artist.class);
    this.artistService.create(artistRequestDto);
    verify(artistRepository).save(captor.capture());
    var createdArtist = captor.getValue();

    Assertions.assertEquals(artistRequestDto.firstName(), createdArtist.getFirstName());
    Assertions.assertEquals(artistRequestDto.lastName(), createdArtist.getLastName());
    Assertions.assertEquals(artistRequestDto.artistName(), createdArtist.getArtistName());
  }
}
