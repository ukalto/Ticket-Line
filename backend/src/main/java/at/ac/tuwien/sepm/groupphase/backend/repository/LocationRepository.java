package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

  /**
   * Finds location in database based on restrictions stemming from given parameter values.
   *
   * @param name string restricting locations by their name (partial name is sufficient)
   * @param country string restricting locations by their country
   * @param town string restricting locations by their town
   * @param street string restricting locations by their street
   * @param postalCode string restricting locations by their postal code
   * @return locations matching the given restrictions
   */
  Page<Location>
      findByNameContainingAndCountryContainingAndTownContainingAndStreetContainingAndPostalCodeContainingAllIgnoreCase(
          @Param("name") String name,
          @Param("country") String country,
          @Param("town") String town,
          @Param("street") String street,
          @Param("postalCode") String postalCode,
          Pageable pageable);

  /**
   * Finds all locations ordered by their descending name.
   *
   * @return list of all locations
   */
  List<Location> findAllByOrderByNameDesc();

  /**
   * Checks whether or not a location with given name already exists.
   *
   * @param name used for matching location names in database
   * @return true if location with corresponding name exists and false if not
   */
  Boolean existsByName(String name);
}
