package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEntryCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsEntryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsEntryRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.*;

/**
 * Security is a cross-cutting concern, however for the sake of simplicity it is tested against the
 * newsEntry endpoint
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityTest implements TestData {

  private static final List<Class<?>> mappingAnnotations =
      Lists.list(
          RequestMapping.class,
          GetMapping.class,
          PostMapping.class,
          PutMapping.class,
          PatchMapping.class,
          DeleteMapping.class);

  private static final List<Class<?>> securityAnnotations =
      Lists.list(
          Secured.class,
          PreAuthorize.class,
          RolesAllowed.class,
          PermitAll.class,
          DenyAll.class,
          DeclareRoles.class);

  @Autowired private MockMvc mockMvc;

  @Autowired private NewsEntryRepository newsEntryRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private NewsEntryMapper newsEntryMapper;

  @Autowired private JwtTokenizer jwtTokenizer;

  @Autowired private SecurityProperties securityProperties;

  @Autowired private List<Object> components;

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
            1L,
            TEST_NEWS_TITLE,
            TEST_NEWS_TEXT,
            TEST_NEWS_SUMMARY,
            TEST_NEWS_PUBLISHED_ON,
            -1L,
            "test.jpg",
            -1L);
  }

  @Test
  public void givenUserLoggedIn_whenFindAll_then200() throws Exception {
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                get(NEWS_ENTRY_BASE_URI)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertAll(
        () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
        () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()));
  }

  @Test
  public void givenNoOneLoggedIn_whenFindAll_then200() throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(get(NEWS_ENTRY_BASE_URI)).andDo(print()).andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void givenAdminLoggedIn_whenPost_then201() throws Exception {
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
  }

  @Test
  public void givenNoOneLoggedIn_whenPost_then403() throws Exception {
    newsEntry.setPublishedOn(null);
    NewsEntryCreationDto newsEntryCreationDto =
        newsEntryMapper.newsEntryToNewsEntryCreationDto(newsEntry);
    String body = objectMapper.writeValueAsString(newsEntryCreationDto);

    MvcResult mvcResult =
        this.mockMvc
            .perform(
                post(NEWS_ENTRY_BASE_URI + "-entry")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
  }

  @Test
  public void givenUserLoggedIn_whenPost_then403() throws Exception {
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
                        jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES, 0L)))
            .andDo(print())
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();

    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
  }
}
