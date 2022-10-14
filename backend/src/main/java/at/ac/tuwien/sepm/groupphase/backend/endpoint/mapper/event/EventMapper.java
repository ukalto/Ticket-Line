package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.event;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TopEventResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventShowing;
import at.ac.tuwien.sepm.groupphase.backend.entity.TopEvent;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedNonSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookedSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorPriceAndName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface EventMapper {
  EventResponseDto eventToEventResponseDto(Event event);

  TopEventResponseDto topEventToTopEventResponseDto(TopEvent event);

  EventCategoryDto eventCategoryToEventCategoryDto(EventCategory eventCategory);

  ShowingDto showingEntityToDto(EventShowing showing);

  @Mappings({
    @Mapping(target = "name", source = "sectorPrice.name"),
    @Mapping(target = "price", source = "sectorPrice.price")
  })
  EventShowingSectorPriceDto showingSectorPriceEntityToDto(SectorPriceAndName sectorPrice);

  @Mappings({@Mapping(target = "row", source = "seat.seatingPlanRow")})
  EventShowingBookedSeatsDto showingBookedSeatsEntityToDto(BookedSeat seat);

  EventShowingBookedNonSeatsDto showingBookedNonSeatsEntityToDto(BookedNonSeat nonSeat);
}
