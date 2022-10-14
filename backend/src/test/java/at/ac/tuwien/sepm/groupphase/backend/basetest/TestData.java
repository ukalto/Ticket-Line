package at.ac.tuwien.sepm.groupphase.backend.basetest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

  Long ID = 1L;
  String TEST_NEWS_TITLE = "Title";
  String TEST_NEWS_SUMMARY = "Summary";
  String TEST_NEWS_TEXT = "TestMessageText";
  Timestamp TEST_NEWS_PUBLISHED_ON =
      Timestamp.valueOf(LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0));

  String BASE_URI = "/api/v1";
  String NEWS_ENTRY_BASE_URI = BASE_URI + "/news";

  String ADMIN_USER = "testAdmin@email.com";
  List<String> ADMIN_ROLES =
      new ArrayList<>() {
        {
          add("ROLE_ADMINISTRATOR");
          add("ROLE_USER");
        }
      };
  String DEFAULT_USER = "testUser@email.com";
  List<String> USER_ROLES =
      new ArrayList<>() {
        {
          add("ROLE_USER");
        }
      };
}
