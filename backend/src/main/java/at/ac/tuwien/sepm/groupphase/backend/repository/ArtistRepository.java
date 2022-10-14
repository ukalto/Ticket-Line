package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

  /**
   * Finds all artists whose name 'first_name last_name' or 'artist_name' matches with the search
   * param.
   *
   * @param name search param matched with firs-,last-, artist_name
   * @param pageable Defines the page of the result
   * @return a page of artists that match the criteria
   */
  @Query(
      value =
          """
             SELECT ID, CONCAT(ARTIST_NAME,FIRST_NAME,' ',LAST_NAME) AS ARTIST_NAME, '' as FIRST_NAME, '' as LAST_NAME,
             (SELECT COUNT(*) FROM ARTIST_PERFORMANCE AP WHERE ID = AP.ARTIST_ID) AS EID
             FROM ARTIST
             WHERE ARTIST_NAME LIKE CONCAT('%',?1,'%') OR CONCAT(FIRST_NAME,' ',LAST_NAME) LIKE CONCAT('%',?1,'%')
             ORDER BY EID DESC, ARTIST_NAME ASC
             """,
      countQuery =
          "SELECT ID, CONCAT(ARTIST_NAME,FIRST_NAME,' ',LAST_NAME) AS ARTIST_NAME, '' as FIRST_NAME, '' as LAST_NAME "
              + "FROM ARTIST WHERE ARTIST_NAME LIKE CONCAT('%',?1,'%') "
              + "OR CONCAT(FIRST_NAME,' ',LAST_NAME) LIKE CONCAT('%',?1,'%')",
      nativeQuery = true)
  Page<Artist> findAllByNameOrAlias(String name, Pageable pageable);

  /**
   * Fetches top ten artistIds by name.
   *
   * @return List of artistIds that fulfill the requirements.
   */
  List<Artist>
      findTop10ByArtistNameContainingOrFirstNameContainingOrLastNameContainingAllIgnoreCase(
          @Param("artistName") String artistName,
          @Param("firstName") String firstName,
          @Param("lastName") String lastName);
}
