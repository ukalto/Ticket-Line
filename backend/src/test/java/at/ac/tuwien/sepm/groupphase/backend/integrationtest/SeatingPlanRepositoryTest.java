package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class SeatingPlanRepositoryTest {
  @Autowired private SeatingPlanRepository seatingPlanRepository;
  @Autowired private SeatingPlanSectorRepository seatingPlanSectorRepository;
  @Autowired private SeatingPlanRowRepository seatingPlanRowRepository;
  @Autowired private SeatingPlanSeatRepository seatingPlanSeatRepository;
  @Autowired private LocationRepository locationRepository;

  @Test
  @DirtiesContext
  public void saveSeatingPlan_shouldStoreSeatingPlan() {
    Location location = new Location();
    location.setName("Cineplexx");
    location.setCountry("Austria");
    location.setTown("Vienna");
    location.setStreet("Wienerbergstraße 11");
    location.setPostalCode("1010");

    locationRepository.save(location);

    SeatingPlanCreationDto.Seat seat1 = new SeatingPlanCreationDto.Seat(true);
    SeatingPlanCreationDto.Seat seat2 = new SeatingPlanCreationDto.Seat(false);
    List<SeatingPlanCreationDto.Seat> seats = new ArrayList<>();
    seats.add(seat1);
    seats.add(seat2);

    SeatingPlanCreationDto.Row row = new SeatingPlanCreationDto.Row(seats);
    List<SeatingPlanCreationDto.Row> rows = new ArrayList<>();
    rows.add(row);

    SeatingPlanCreationDto.Sector sector =
        new SeatingPlanCreationDto.Sector("Sector 1", "#f5aa42", 2, SectorType.seating, rows);

    List<SeatingPlanCreationDto.Sector> sectors = new ArrayList<>();
    sectors.add(sector);

    SeatingPlanCreationDto seatingPlanCreationDto =
        new SeatingPlanCreationDto("Seating Plan 1", 1l, 2, sectors);

    seatingPlanRepository.save(seatingPlanCreationDto);

    Assertions.assertTrue(seatingPlanRepository.existsById(1L));

    SeatingPlan savedSeatingPlan = seatingPlanRepository.findById(1L).get();

    Assertions.assertEquals(seatingPlanCreationDto.name(), savedSeatingPlan.getName());
    Assertions.assertEquals(seatingPlanCreationDto.locatedIn(), savedSeatingPlan.getLocatedIn());
    Assertions.assertEquals(seatingPlanCreationDto.capacity(), savedSeatingPlan.getCapacity());

    SeatingPlanSector.PrimaryKeys sectorKeys = new SeatingPlanSector.PrimaryKeys();
    sectorKeys.setNumber(1L);
    sectorKeys.setSeatingPlan(1L);

    Optional<SeatingPlanSector> savedSector = seatingPlanSectorRepository.findById(sectorKeys);
    Assertions.assertTrue(savedSector.isPresent());

    SeatingPlanRow.PrimaryKeys rowKeys = new SeatingPlanRow.PrimaryKeys();
    rowKeys.setNumber(1L);
    rowKeys.setSeatingPlanId(1L);
    rowKeys.setSeatingPlanSector(1L);

    Optional<SeatingPlanRow> savedRow = seatingPlanRowRepository.findById(rowKeys);
    Assertions.assertTrue(savedRow.isPresent());

    SeatingPlanSeat.PrimaryKeys seatKeys1 = new SeatingPlanSeat.PrimaryKeys();
    seatKeys1.setNumber(1L);
    seatKeys1.setSeatingPlanId(1L);
    seatKeys1.setSeatingPlanSector(1L);
    seatKeys1.setSeatingPlanRow(1L);

    Optional<SeatingPlanSeat> savedSeat1 = seatingPlanSeatRepository.findById(seatKeys1);
    Assertions.assertTrue(savedSeat1.isPresent());

    SeatingPlanSeat.PrimaryKeys seatKeys2 = new SeatingPlanSeat.PrimaryKeys();
    seatKeys2.setNumber(1L);
    seatKeys2.setSeatingPlanId(1L);
    seatKeys2.setSeatingPlanSector(1L);
    seatKeys2.setSeatingPlanRow(1L);

    Optional<SeatingPlanSeat> savedSeat2 = seatingPlanSeatRepository.findById(seatKeys2);
    Assertions.assertTrue(savedSeat2.isPresent());
  }
}
