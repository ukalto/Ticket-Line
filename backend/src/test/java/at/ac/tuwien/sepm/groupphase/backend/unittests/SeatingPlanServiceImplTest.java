package at.ac.tuwien.sepm.groupphase.backend.unittests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SeatingPlanSeatMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.SeatingPlanService;
import at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.impl.SeatingPlanServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.impl.SeatingPlanValidator;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeatingPlanServiceImplTest {
  private SeatingPlanRepository seatingPlanRepository;
  private LocationRepository locationRepository;
  @Autowired private SeatingPlanValidator seatingPlanValidator;
  private SeatingPlanSectorRepository seatingPlanSectorRepository;
  private SeatingPlanRowRepository seatingPlanRowRepository;
  private SeatingPlanSeatRepository seatingPlanSeatRepository;
  private SeatingPlanSeatMapper seatingPlanSeatMapper;
  private SeatingPlanService seatingPlanService;

  @BeforeEach
  void setup() {
    this.seatingPlanRepository = mock(SeatingPlanRepository.class);
    this.locationRepository = mock(LocationRepository.class);
    this.seatingPlanService =
        new SeatingPlanServiceImpl(
            this.seatingPlanRepository,
            this.locationRepository,
            this.seatingPlanValidator,
            this.seatingPlanSectorRepository,
            this.seatingPlanRowRepository,
            this.seatingPlanSeatRepository,
            this.seatingPlanSeatMapper);
  }

  @Test
  public void saveSeatingPlanForNonExistingLocation_shouldThrowNotFound() {

    SeatingPlanCreationDto.Seat seat1 = new SeatingPlanCreationDto.Seat(true);
    List<SeatingPlanCreationDto.Seat> seats = new ArrayList<>();
    seats.add(seat1);

    SeatingPlanCreationDto.Row row = new SeatingPlanCreationDto.Row(seats);
    List<SeatingPlanCreationDto.Row> rows = new ArrayList<>();
    rows.add(row);

    SeatingPlanCreationDto.Sector sector =
        new SeatingPlanCreationDto.Sector("Sector 1", "#f5aa42", 2, SectorType.seating, rows);
    List<SeatingPlanCreationDto.Sector> sectors = new ArrayList<>();
    sectors.add(sector);

    SeatingPlanCreationDto seatingPlanCreationDto =
        new SeatingPlanCreationDto("seating plan", -1L, 2, sectors);

    when(this.locationRepository.existsById(-1L)).thenThrow(NotFoundException.class);

    Assertions.assertThrows(
        NotFoundException.class, () -> seatingPlanService.save(seatingPlanCreationDto));
  }

  @Test
  public void saveSeatingPlan_shouldThrowValidationException() {
    SeatingPlanCreationDto.Seat seat1 = new SeatingPlanCreationDto.Seat(true);
    List<SeatingPlanCreationDto.Seat> seats = new ArrayList<>();
    seats.add(seat1);

    SeatingPlanCreationDto.Row row = new SeatingPlanCreationDto.Row(seats);
    List<SeatingPlanCreationDto.Row> rows = new ArrayList<>();
    rows.add(row);

    SeatingPlanCreationDto.Sector sector =
        new SeatingPlanCreationDto.Sector("Sector 1", "#f5aa42", 1, SectorType.standing, rows);
    List<SeatingPlanCreationDto.Sector> sectors = new ArrayList<>();
    sectors.add(sector);

    SeatingPlanCreationDto seatingPlanCreationDto =
        new SeatingPlanCreationDto(null, 1L, 2, sectors);

    when(this.locationRepository.existsById(1L)).thenReturn(true);

    ValidationException exception =
        Assertions.assertThrows(
            ValidationException.class, () -> seatingPlanService.save(seatingPlanCreationDto));
    Assertions.assertEquals("In a standing sectorId rows must be null", exception.getMessage());

    SeatingPlanCreationDto.Sector sector2 =
        new SeatingPlanCreationDto.Sector("Sector 2", "#f5aa42", 1, SectorType.seating, rows);
    List<SeatingPlanCreationDto.Sector> sectors2 = new ArrayList<>();
    sectors2.add(sector2);
  }
}
