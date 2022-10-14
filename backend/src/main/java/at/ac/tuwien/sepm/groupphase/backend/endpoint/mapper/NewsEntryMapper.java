package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntryReadBy;
import org.mapstruct.Mapper;

@Mapper
public interface NewsEntryMapper {

  NewsEntry detailedNewsEntryDtoToNewsEntry(DetailedNewsEntryDto detailedNewsEntryDto);

  DetailedNewsEntryDto newsEntryToDetailedNewsEntryDto(NewsEntry newsEntry);

  NewsEntry newsEntryCreationDtoToNewsEntry(NewsEntryCreationDto newsEntryCreationDto);

  NewsEntryCreationDto newsEntryToNewsEntryCreationDto(NewsEntry newsEntry);

  NewsEntryOverviewDto newsEntryToNewsEntryOverviewDto(NewsEntry newsEntry);

  NewsEntryReadByDto newsEntryReadByToNewsEntryReadByDto(NewsEntryReadBy newsEntryReadBy);
}
