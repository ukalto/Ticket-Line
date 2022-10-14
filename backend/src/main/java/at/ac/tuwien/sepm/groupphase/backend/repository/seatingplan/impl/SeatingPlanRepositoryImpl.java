package at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorType;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.CustomSeatingPlanRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

public class SeatingPlanRepositoryImpl implements CustomSeatingPlanRepository {
  private static final String SEATING_PLAN_TABLE_NAME = "seating_plan";
  private static final String SEATING_PLAN_SECTOR_TABLE_NAME = "seating_plan_sector";
  private static final String SEATING_PLAN_ROW_TABLE_NAME = "seating_plan_row";
  private static final String SEATING_PLAN_SEAT_TABLE_NAME = "seating_plan_seat";

  private final JdbcTemplate jdbcTemplate;

  public SeatingPlanRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional
  @Override
  public SeatingPlan save(SeatingPlanCreationDto seatingPlanCreationDto) {
    String sqlInsertSeatingPlan =
        "INSERT INTO "
            + SEATING_PLAN_TABLE_NAME
            + " ("
            + "name, "
            + "located_in, "
            + "capacity) "
            + "VALUES (?, ?, ?);";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(
          connection -> {
            PreparedStatement insertSeatingPlan =
                connection.prepareStatement(sqlInsertSeatingPlan, Statement.RETURN_GENERATED_KEYS);
            insertSeatingPlan.setString(1, seatingPlanCreationDto.name());
            insertSeatingPlan.setString(2, seatingPlanCreationDto.locatedIn().toString());
            insertSeatingPlan.setString(3, seatingPlanCreationDto.capacity().toString());

            return insertSeatingPlan;
          },
          keyHolder);
    } catch (DataAccessException e) {
      throw new PersistenceException("Could not save seating plan", e);
    }
    Long seatingPlanId = ((Number) keyHolder.getKeys().get("id")).longValue();

    Long sectorNumber = 1L;
    for (SeatingPlanCreationDto.Sector sector : seatingPlanCreationDto.sectors()) {

      saveSeatingPlanSector(sectorNumber, seatingPlanId, sector);
      sectorNumber++;
    }

    SeatingPlan seatingPlan = new SeatingPlan();
    seatingPlan.setId(seatingPlanId);
    seatingPlan.setName(seatingPlanCreationDto.name());
    seatingPlan.setLocatedIn(seatingPlanCreationDto.locatedIn());
    seatingPlan.setCapacity(seatingPlanCreationDto.capacity());

    return seatingPlan;
  }

  private Long saveSeatingPlanSector(
      Long number, Long seatingPlanId, SeatingPlanCreationDto.Sector sector) {

    String sqlInsertSector =
        "INSERT INTO "
            + SEATING_PLAN_SECTOR_TABLE_NAME
            + " ("
            + "number, "
            + "seating_plan_id, "
            + "name, "
            + "color, "
            + "capacity, "
            + "type) "
            + "VALUES (?, ?, ?, ?, ?, ?);";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(
          connection -> {
            PreparedStatement insertSector =
                connection.prepareStatement(sqlInsertSector, Statement.RETURN_GENERATED_KEYS);
            insertSector.setString(1, number.toString());
            insertSector.setString(2, seatingPlanId.toString());
            insertSector.setString(3, sector.name());
            insertSector.setString(4, sector.color());
            insertSector.setString(5, sector.capacity().toString());
            insertSector.setString(6, sector.type().toString());

            return insertSector;
          },
          keyHolder);
    } catch (DataAccessException e) {
      throw new PersistenceException("Could not save sectorId", e);
    }

    Long sectorNumber = ((Number) keyHolder.getKeys().get("number")).longValue();

    Long rowNumber = 1L;
    if (sector.type().equals(SectorType.seating)) {
      for (SeatingPlanCreationDto.Row row : sector.rows()) {

        saveSeatingPlanRow(rowNumber, seatingPlanId, sectorNumber, row);
        rowNumber++;
      }
    }

    return sectorNumber;
  }

  private Long saveSeatingPlanRow(
      Long number, Long seatingPlanId, Long sectorNumber, SeatingPlanCreationDto.Row row) {

    String sqlInsertRow =
        "INSERT INTO "
            + SEATING_PLAN_ROW_TABLE_NAME
            + " ("
            + "number, "
            + "seating_plan_id, "
            + "seating_plan_sector) "
            + "VALUES (?, ?, ?);";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(
          connection -> {
            PreparedStatement insertRow =
                connection.prepareStatement(sqlInsertRow, Statement.RETURN_GENERATED_KEYS);
            insertRow.setString(1, number.toString());
            insertRow.setString(2, seatingPlanId.toString());
            insertRow.setString(3, sectorNumber.toString());

            return insertRow;
          },
          keyHolder);
    } catch (DataAccessException e) {
      throw new PersistenceException("Could not save row", e);
    }
    Long rowNumber = ((Number) keyHolder.getKeys().get("number")).longValue();

    Long seatNumber = 1L;
    for (SeatingPlanCreationDto.Seat seat : row.seats()) {

      saveSeatingPlanSeat(seatNumber, seatingPlanId, sectorNumber, rowNumber, seat);
      seatNumber++;
    }

    return rowNumber;
  }

  private Long saveSeatingPlanSeat(
      Long number,
      Long seatingPlanId,
      Long sectorNumber,
      Long rowNumber,
      SeatingPlanCreationDto.Seat seat) {

    String sqlInsertSeat =
        "INSERT INTO "
            + SEATING_PLAN_SEAT_TABLE_NAME
            + " ("
            + "number, "
            + "seating_plan_id, "
            + "seating_plan_sector, "
            + "seating_plan_row, "
            + "enabled) "
            + "VALUES(?, ?, ?, ?, ?);";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(
          connection -> {
            PreparedStatement insertSeat =
                connection.prepareStatement(sqlInsertSeat, Statement.RETURN_GENERATED_KEYS);
            insertSeat.setString(1, number.toString());
            insertSeat.setString(2, seatingPlanId.toString());
            insertSeat.setString(3, sectorNumber.toString());
            insertSeat.setString(4, rowNumber.toString());
            insertSeat.setString(5, seat.enabled().toString());

            return insertSeat;
          },
          keyHolder);
    } catch (DataAccessException e) {
      throw new PersistenceException("Could not save seat", e);
    }

    return ((Number) keyHolder.getKeys().get("number")).longValue();
  }
}
