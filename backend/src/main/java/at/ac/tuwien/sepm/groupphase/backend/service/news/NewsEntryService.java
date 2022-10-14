package at.ac.tuwien.sepm.groupphase.backend.service.news;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntryReadBy;
import java.util.List;

public interface NewsEntryService {

  /**
   * Find all news entries ordered by published at date (descending) that were not read by the user
   * with the given Id.
   *
   * @param userId Id of the given user
   * @return ordered list of all unread news entries
   */
  List<NewsEntryOverviewDto> findAll(Long userId);

  /**
   * Find all news entries ordered by published at date (descending) that were read by the user with
   * the given Id.
   *
   * @param userId Id of the given user
   * @return ordered list of all read news entries
   */
  List<NewsEntryOverviewDto> findAllRead(Long userId);

  /**
   * Finds a single NewsEntryDetailsDto by its id.
   *
   * @param id the id of the NewsEntryDetailsDto
   * @return NewsEntryDetailsDto
   */
  NewsEntryDetailsDto findNewsEntryDetailsById(Long id);

  /**
   * Publish a single news entry.
   *
   * @param news to publish
   * @return published news entry
   */
  NewsEntry publishNewsEntry(NewsEntryCreationDto news);

  /**
   * Stores a viewing of a news entry.
   *
   * @return NewsEntryReadByDto that was created
   */
  NewsEntryReadBy registerNewsEntryViewing(Long newsId, Long userId);
}
