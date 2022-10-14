package at.ac.tuwien.sepm.groupphase.backend.service.event;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailsShowsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventShowingFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventShowing;
import at.ac.tuwien.sepm.groupphase.backend.entity.TopEvent;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorPriceAndName;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
  /**
   * Lists the top ten events.
   *
   * @param categoryId of the category to filter for.
   * @return a list of top ten events.
   */
  List<TopEvent> findTopTen(Long categoryId);

  /**
   * Finds all events ordered by title.
   *
   * @return List of events
   */
  List<Event> findAll();

  /**
   * Creates Event, EventShowings, SectorPriceEventShowings and ArtistPerformances based on the
   * content of the DTO.
   *
   * @param eventCreationDto DTO which contains all relevant Event information
   * @return Event which is created
   */
  Event create(EventCreationDto eventCreationDto);

  /**
   * Gets all showing matching the search criteria.
   *
   * @param eventShowingFilterDto stores search criteria
   * @param pageable stores index and site of returned page
   * @return page of details about showings
   */
  Page<EventDetailsShowsDto> filterShowings(
      EventShowingFilterDto eventShowingFilterDto, Pageable pageable);

  /**
   * Returns all available Event Categories.
   *
   * @return List of Event Categories
   */
  List<EventCategory> findCategories();

  /**
   * Finds an event by its id.
   *
   * @param id which identifies the event
   * @return eventDTO with the corresponding id
   */
  Optional<EventDto> findById(Long id) throws IOException;

  /**
   * Finds a eventCategory by its eventId.
   *
   * @param id which identifies the event
   * @return eventCategoryDTO with the corresponding eventId
   */
  Optional<EventCategory> findEventCategoryById(Long id);

  /**
   * Finds a page of eventShowingDTOs with the event id and a pageable.
   *
   * @param eventId which identifies the event
   * @param pageable which tells me the pageNumber- and size
   * @return Page of eventShowingDTOs with the same eventId
   */
  Optional<Page<EventDetailsShowsDto>> findShowingsById(Long eventId, Pageable pageable);

  /**
   * Returns a list of prices and names for each seatinplan-sector of a specific showing.
   *
   * @return List of prices and sector names
   */
  List<SectorPriceAndName> findPrices(Long showingId);

  /**
   * Returns the showing that corresponds to the id.
   *
   * @param showingId the id of the showing
   * @return showing with the showingId
   */
  EventShowing findShowingById(Long showingId);

  /**
   * Returns infos to the events that fit the search criteria. The event duration given in hours and
   * minutes is not exact. There can be a max difference of +/-30min.
   *
   * @param nameOrContent a string, that is contained either in the name or the content(description)
   *     of the event
   * @param categoryId the id of the category the events are assigned to
   * @param hours how long the event is in hours
   * @param minutes are added to the hours
   * @param pageNumber the page of the pagination
   * @param pageSize how many entries are queried with one request
   * @return a list of events
   */
  List<EventSearchResultDto> filterEvents(
      String nameOrContent,
      Long categoryId,
      Long hours,
      Long minutes,
      Long pageNumber,
      Long pageSize);

  /**
   * Returns the showing that corresponds to the id and event id.
   *
   * @param id the id of the showing
   * @param eventId the eventId of the showing
   * @return showing with matching id and eventId
   */
  EventShowing findShowingByIdAndEventId(Long id, Long eventId);

  /**
   * Finds top 3 events with matching title.
   *
   * @param title title of the event
   * @return a list of max 3 Events
   */
  List<EventResponseDto> findEventByTitle(String title);

  /**
   * Returns true if the given SeatingPlan is occupied at the given time, false otherwise.
   *
   * @param seatingPlanId id of the SeatingPlan
   * @param start starting time
   * @param end ending time
   * @return true or false
   */
  Boolean isOccupied(Long seatingPlanId, Timestamp start, Timestamp end);
}
