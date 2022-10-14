package at.ac.tuwien.sepm.groupphase.backend.service.news.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsEntryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntryReadBy;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsEntryReadByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsEntryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.news.NewsEntryService;
import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class NewsEntryServiceImpl implements NewsEntryService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final NewsEntryRepository newsEntryRepository;
  private final NewsEntryMapper newsEntryMapper;
  private final ImageService imageService;
  private final NewsEntryReadByRepository newsEntryReadByRepository;

  @Override
  public List<NewsEntryOverviewDto> findAll(Long userId) {
    LOGGER.debug("Find all newsEntrys");
    List<NewsEntry> newsList;
    if (userId != null) {
      newsList = newsEntryRepository.findUnreadNews(userId);
    } else {
      newsList = newsEntryRepository.findAllByOrderByPublishedOnDesc();
    }
    for (NewsEntry newsEntry : newsList) {
      if (newsEntry.getSummary() == null) {
        if (newsEntry.getContents().length() > 400) {
          newsEntry.setSummary(newsEntry.getContents().substring(0, 400));
        } else {
          newsEntry.setSummary(newsEntry.getContents());
        }
      }
      newsEntry.setImageRef(imageService.imageToBase64(newsEntry.getImageRef()));
    }
    return newsList.stream()
        .map(newsEntryMapper::newsEntryToNewsEntryOverviewDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<NewsEntryOverviewDto> findAllRead(Long userId) {
    LOGGER.debug("Find all unread newsEntrys");
    List<NewsEntry> newsList = newsEntryRepository.findReadNews(userId);
    for (NewsEntry newsEntry : newsList) {
      if (newsEntry.getSummary() == null) {
        if (newsEntry.getContents().length() > 400) {
          newsEntry.setSummary(newsEntry.getContents().substring(0, 400));
        } else {
          newsEntry.setSummary(newsEntry.getContents());
        }
      }
      newsEntry.setImageRef(imageService.imageToBase64(newsEntry.getImageRef()));
    }
    return newsList.stream()
        .map(newsEntryMapper::newsEntryToNewsEntryOverviewDto)
        .collect(Collectors.toList());
  }

  @Override
  public NewsEntryDetailsDto findNewsEntryDetailsById(Long id) {
    LOGGER.debug("Find newsEntry with id {}", id);
    NewsEntry newsEntry = newsEntryRepository.findNewsEntryById(id);
    try {
      LocalDateTime date = newsEntry.getPublishedOn().toLocalDateTime();
      DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      return new NewsEntryDetailsDto(
          newsEntry.getId(),
          newsEntry.getTitle(),
          date.toLocalDate().format(dtfDate),
          newsEntry.getContents(),
          newsEntry.getEventId(),
          imageService.imageToBase64(newsEntry.getImageRef()));
    } catch (NullPointerException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public NewsEntry publishNewsEntry(NewsEntryCreationDto newsEntryDto) {
    LOGGER.debug("Publish new newsEntry {}", newsEntryDto);
    NewsEntry newsEntry = newsEntryMapper.newsEntryCreationDtoToNewsEntry(newsEntryDto);
    newsEntry.setPublishedOn(Timestamp.valueOf(LocalDateTime.now()));
    var savedNewsEntry = newsEntryRepository.save(newsEntry);
    Long id = savedNewsEntry.getId();
    newsEntryRepository.changeImageRefFromNewsEntryWithNewsEntryId(id, "n_" + id);
    return savedNewsEntry;
  }

  @Override
  public NewsEntryReadBy registerNewsEntryViewing(Long newsId, Long userId) {
    LOGGER.debug("Register News Entry viewing with newsId {} and userId {}", newsId, userId);
    NewsEntryReadBy newsEntryReadBy =
        new NewsEntryReadBy(userId, newsId, Timestamp.valueOf(LocalDateTime.now()));
    return newsEntryReadByRepository.save(newsEntryReadBy);
  }
}
