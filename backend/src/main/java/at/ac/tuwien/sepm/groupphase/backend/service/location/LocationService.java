package at.ac.tuwien.sepm.groupphase.backend.service.location;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.location.LocationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationService {

  /**
   * Saves a location.
   *
   * @param locationRequestDto to be saved
   * @return entity of saved location
   */
  Location save(LocationRequestDto locationRequestDto);

  /**
   * Finds all locations.
   *
   * @return list of all locations.
   */
  List<Location> findAll();

  /**
   * Finds a location.
   *
   * @param locationRequestDto used for filtering locations
   * @param limit limits number of locations returned
   * @return list of filtered locations
   */
  List<Location> find(LocationRequestDto locationRequestDto, Integer limit);

  /**
   * Filters for Locations that match the pattern and lists events for each of them.
   *
   * @param name name of the location
   * @param country country that the location is located in
   * @param town town that the location is located in
   * @param street street that the location is located at
   * @param postalCode zipcode of the town
   * @param pageable defines pagination
   * @return A LocationEventDto that holds a list of Locations with their events
   */
  Page<LocationEventDto> filterLocationEvents(
      String name,
      String country,
      String town,
      String street,
      String postalCode,
      Pageable pageable);
}
