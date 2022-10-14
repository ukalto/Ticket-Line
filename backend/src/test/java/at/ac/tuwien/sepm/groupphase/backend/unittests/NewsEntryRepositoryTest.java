package at.ac.tuwien.sepm.groupphase.backend.unittests;

import static org.junit.jupiter.api.Assertions.*;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntry;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans
// instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class NewsEntryRepositoryTest implements TestData {

  @Autowired private NewsEntryRepository newsEntryRepository;

  @Test
  public void givenNothing_whenSaveNewsEntry_thenFindListWithOneElementAndFindNewsEntryById() {
    NewsEntry newsEntry =
        new NewsEntry(
            1L,
            TEST_NEWS_TITLE,
            TEST_NEWS_TEXT,
            TEST_NEWS_SUMMARY,
            TEST_NEWS_PUBLISHED_ON,
            -1L,
            "test.jpg",
            -1L);

    newsEntryRepository.save(newsEntry);

    assertAll(
        () -> assertEquals(1, newsEntryRepository.findAll().size()),
        () -> assertNotNull(newsEntryRepository.findById(newsEntry.getId())));
  }
}
