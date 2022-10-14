package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component
@Profile("performance")
public class PerformanceGeneratorBean {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final DataSource dataSource;

  public PerformanceGeneratorBean(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @PostConstruct
  public void generateData() throws SQLException {
    LOGGER.info("Generating performance test data...");
    try (var connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/performanceData.sql"));
      LOGGER.info("Finished generating performance test data without error.");
    }
  }
}
