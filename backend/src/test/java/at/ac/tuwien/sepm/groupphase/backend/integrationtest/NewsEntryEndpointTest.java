package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.DetailedNewsEntryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.SimpleNewsEntryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsEntryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsEntryRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NewsEntryEndpointTest implements TestData {

  @Autowired private MockMvc mockMvc;

  @Autowired private NewsEntryRepository newsEntryRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private NewsEntryMapper newsEntryMapper;

  @Autowired private JwtTokenizer jwtTokenizer;

  @Autowired private SecurityProperties securityProperties;

  @Autowired private ImageService imageService;

  private NewsEntry newsEntry =
      new NewsEntry(
          1L,
          TEST_NEWS_TITLE,
          TEST_NEWS_TEXT,
          TEST_NEWS_SUMMARY,
          TEST_NEWS_PUBLISHED_ON,
          -1L,
          "test.jpg",
          -1L);

  @BeforeEach
  public void beforeEach() {
    newsEntryRepository.deleteAll();
    newsEntry =
        new NewsEntry(
            null,
            TEST_NEWS_TITLE,
            TEST_NEWS_TEXT,
            TEST_NEWS_SUMMARY,
            TEST_NEWS_PUBLISHED_ON,
            -1L,
            "test.jpg",
            -1L);
  }

  @Test
  public void givenOneNewsEntry_whenFindById_thenNewsEntryWithAllProperties() throws Exception {
    newsEntryRepository.save(newsEntry);

    MvcResult mvcResult =
        this.mockMvc
            .perform(
                get(NEWS_ENTRY_BASE_URI + "-entry/{id}", newsEntry.getId())
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertAll(
        () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
        () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()));

    NewsEntryDetailsDto newsEntryDetailsDto =
        objectMapper.readValue(response.getContentAsString(), NewsEntryDetailsDto.class);

    DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    assertEquals(
        new NewsEntryDetailsDto(
            newsEntry.getId(),
            newsEntry.getTitle(),
            newsEntry.getPublishedOn().toLocalDateTime().toLocalDate().format(dtfDate),
            newsEntry.getContents(),
            newsEntry.getEventId(),
            imageService.imageToBase64(newsEntry.getImageRef())),
        newsEntryDetailsDto);
  }

  @Test
  public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                get(NEWS_ENTRY_BASE_URI)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

    List<SimpleNewsEntryDto> simpleNewsEntryDtos =
        Arrays.asList(
            objectMapper.readValue(response.getContentAsString(), SimpleNewsEntryDto[].class));

    assertEquals(0, simpleNewsEntryDtos.size());
  }

  @Test
  public void
      givenOneNewsEntry_whenFindAll_thenListWithSizeOneAndNewsEntryWithAllPropertiesExceptSummary()
          throws Exception {
    newsEntryRepository.save(newsEntry);

    MvcResult mvcResult =
        this.mockMvc
            .perform(
                get(NEWS_ENTRY_BASE_URI)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

    List<SimpleNewsEntryDto> simpleNewsEntryDtos =
        Arrays.asList(
            objectMapper.readValue(response.getContentAsString(), SimpleNewsEntryDto[].class));

    assertEquals(1, simpleNewsEntryDtos.size());
    SimpleNewsEntryDto simpleNewsEntryDto = simpleNewsEntryDtos.get(0);
    assertAll(
        () -> assertEquals(newsEntry.getId(), simpleNewsEntryDto.id()),
        () -> assertEquals(TEST_NEWS_TITLE, simpleNewsEntryDto.title()),
        () -> assertEquals(TEST_NEWS_SUMMARY, simpleNewsEntryDto.summary()));
  }

  @Test
  public void givenOneNewsEntry_whenFindByNonExistingId_then404() throws Exception {
    newsEntryRepository.save(newsEntry);

    MvcResult mvcResult =
        this.mockMvc
            .perform(
                get(NEWS_ENTRY_BASE_URI + "-entry/{id}", -1)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void givenNothing_whenPost_thenNewsEntryWithAllSetPropertiesPlusIdAndPublishedDate()
      throws Exception {
    newsEntry.setPublishedOn(null);
    NewsEntryCreationDto newsEntryCreationDto =
        newsEntryMapper.newsEntryToNewsEntryCreationDto(newsEntry);
    String body = objectMapper.writeValueAsString(newsEntryCreationDto);

    MvcResult mvcResult =
        this.mockMvc
            .perform(
                post(NEWS_ENTRY_BASE_URI + "-entry")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

    DetailedNewsEntryDto newsEntryResponse =
        objectMapper.readValue(response.getContentAsString(), DetailedNewsEntryDto.class);

    assertNotNull(newsEntryResponse.id());
    assertNotNull(newsEntryResponse.publishedOn());
    assertTrue(isNow(newsEntryResponse.publishedOn().toLocalDateTime()));
    // Set generated properties to null to make the response comparable with the original input
    newsEntryResponse =
        new DetailedNewsEntryDto(
            null,
            newsEntryResponse.title(),
            newsEntryResponse.contents(),
            newsEntryResponse.summary(),
            null,
            newsEntryResponse.publishedBy(),
            newsEntryResponse.imageRef(),
            newsEntryResponse.eventId());
    assertEquals(newsEntry, newsEntryMapper.detailedNewsEntryDtoToNewsEntry(newsEntryResponse));
  }

  @Test
  public void givenNothing_whenPostInvalid_then400() throws Exception {
    newsEntry.setTitle(null);
    newsEntry.setSummary(null);
    newsEntry.setContents(null);
    NewsEntryCreationDto newsEntryCreationDto =
        newsEntryMapper.newsEntryToNewsEntryCreationDto(newsEntry);
    String body = objectMapper.writeValueAsString(newsEntryCreationDto);

    MvcResult mvcResult =
        this.mockMvc
            .perform(
                post(NEWS_ENTRY_BASE_URI + "-entry")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertAll(
        () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
        () -> {
          // Reads the errors from the body
          String content = response.getContentAsString();
          content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
          String[] errors = content.split(",");
          assertEquals(2, errors.length);
        });
  }

  private boolean isNow(LocalDateTime date) {
    LocalDateTime today = LocalDateTime.now();
    return date.getYear() == today.getYear()
        && date.getDayOfYear() == today.getDayOfYear()
        && date.getHour() == today.getHour();
  }
}
