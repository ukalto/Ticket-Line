package at.ac.tuwien.sepm.groupphase.backend.repository.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.SectorPriceEventShowing;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorPriceAndName;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SectorPriceEventShowingRepository
    extends JpaRepository<SectorPriceEventShowing, Long> {
  /**
   * Gets the SectorPriceEventShowing with the: eventId, seatingPlanId, seatingPlanSectorId.
   *
   * @param eventId used for matching eventId in database
   * @param seatingPlanId used for matching seatingPlanId in database
   * @param seatingPlanSectorId used for matching seatingPlanSectorId in database
   * @return SectorPriceEventShowing
   */
  SectorPriceEventShowing findByEventShowingIdAndSeatingPlanIdAndSeatingPlanSector(
      Long eventId, Long seatingPlanId, Long seatingPlanSectorId);

  /**
   * Returns a list of sector prices and names from a given showing.
   *
   * @param showingId the id of the showing
   * @return a list of SectorPriceAndName objects
   */
  @Query(
      value =
          "SELECT price, name "
              + "FROM sector_price_event_showing "
              + "LEFT JOIN seating_plan_sector "
              + "ON sector_price_event_showing.seating_plan_id = seating_plan_sector.seating_plan_id "
              + "AND sector_price_event_showing.seating_plan_sector = seating_plan_sector.number "
              + "WHERE sector_price_event_showing.event_showing_id = ?1",
      nativeQuery = true)
  List<SectorPriceAndName> findPriceAndNameByShowing(Long showingId);
}
