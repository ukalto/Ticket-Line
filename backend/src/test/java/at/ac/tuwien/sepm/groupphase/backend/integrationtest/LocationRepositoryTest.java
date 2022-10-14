package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class LocationRepositoryTest {
  @Autowired LocationRepository locationRepository;

  @Test
  @DirtiesContext
  public void findBy_shouldFilterByName() {
    List<Location> allLocations = locationRepository.findAll();

    Assertions.assertEquals(4, allLocations.size());

    Pageable pageable = PageRequest.of(0, 2);

    List<Location> results =
        locationRepository
            .findByNameContainingAndCountryContainingAndTownContainingAndStreetContainingAndPostalCodeContainingAllIgnoreCase(
                "cInEpLeXx", "", "", "", "", pageable)
            .getContent();

    Assertions.assertEquals(2, results.size());

    Location result1 = results.get(0);
    Location result2 = results.get(1);

    Assertions.assertEquals("Cineplexx Graz", result1.getName());
    Assertions.assertEquals("Austria", result1.getCountry());
    Assertions.assertEquals("Graz", result1.getTown());
    Assertions.assertEquals("Alte Poststrasse 79", result1.getStreet());
    Assertions.assertEquals("8055", result1.getPostalCode());

    Assertions.assertEquals("Cineplexx Donau Zentrum", result2.getName());
    Assertions.assertEquals("Austria", result2.getCountry());
    Assertions.assertEquals("Vienna", result2.getTown());
    Assertions.assertEquals("Wagramstrasse 79", result2.getStreet());
    Assertions.assertEquals("1220", result2.getPostalCode());
  }
}
