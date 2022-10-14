package at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seatingplan.SeatingPlanResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SeatingPlanSeatMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanRow;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatingPlanSector;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.seatingplan.SeatingPlanSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.seatingplan.SeatingPlanService;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatingPlanServiceImpl implements SeatingPlanService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final SeatingPlanRepository seatingPlanRepository;
  private final LocationRepository locationRepository;
  private final SeatingPlanValidator seatingPlanValidator;
  private final SeatingPlanSectorRepository seatingPlanSectorRepository;
  private final SeatingPlanRowRepository seatingPlanRowRepository;
  private final SeatingPlanSeatRepository seatingPlanSeatRepository;
  private final SeatingPlanSeatMapper seatingPlanSeatMapper;

  public SeatingPlanServiceImpl(
      SeatingPlanRepository seatingPlanRepository,
      LocationRepository locationRepository,
      SeatingPlanValidator seatingPlanValidator,
      SeatingPlanSectorRepository seatingPlanSectorRepository,
      SeatingPlanRowRepository seatingPlanRowRepository,
      SeatingPlanSeatRepository seatingPlanSeatRepository,
      SeatingPlanSeatMapper seatingPlanSeatMapper) {
    this.seatingPlanRepository = seatingPlanRepository;
    this.locationRepository = locationRepository;
    this.seatingPlanValidator = seatingPlanValidator;
    this.seatingPlanSectorRepository = seatingPlanSectorRepository;
    this.seatingPlanRowRepository = seatingPlanRowRepository;
    this.seatingPlanSeatRepository = seatingPlanSeatRepository;
    this.seatingPlanSeatMapper = seatingPlanSeatMapper;
  }

  @Override
  public SeatingPlan save(SeatingPlanCreationDto seatingPlanCreationDto) {
    LOGGER.debug("Saving seating plan with name {}", seatingPlanCreationDto.name());

    Long locationId = seatingPlanCreationDto.locatedIn();
    boolean locationDoesNotExists = !locationRepository.existsById(locationId);
    if (locationDoesNotExists) {
      throw new NotFoundException("Location referenced within given seating plan does not exist");
    }

    boolean nameAlreadyExists = checkDuplicateName(locationId, seatingPlanCreationDto.name());
    if (nameAlreadyExists) {
      throw new AlreadyExistsException("Given name already exists in referenced location");
    }

    seatingPlanValidator.validateSeatingPlanCreationDto(seatingPlanCreationDto);

    return seatingPlanRepository.save(seatingPlanCreationDto);
  }

  private boolean checkDuplicateName(Long locationId, String seatingPlanName) {
    List<SeatingPlan> seatingPlansInLocation = seatingPlanRepository.findAllByLocatedIn(locationId);

    for (SeatingPlan seatingPlan : seatingPlansInLocation) {
      if (seatingPlan.getName().equals(seatingPlanName)) {
        return true;
      }
    }
    return false;
  }

  public List<SeatingPlan> findSeatingPlansByLocation(Long id) {
    return seatingPlanRepository.findAllByLocatedIn(id);
  }

  @Transactional
  public SeatingPlanResponseDto findSeatingPlanById(Long id) {

    var maybeSeatingPlan = seatingPlanRepository.findById(id);
    SeatingPlan seatingPlan;
    if (maybeSeatingPlan.isPresent()) {
      seatingPlan = maybeSeatingPlan.get();
    } else {
      throw new NotFoundException(String.format("Could not find seating plan with id %s", id));
    }

    List<SeatingPlanSector> sectors = seatingPlanSectorRepository.findAllBySeatingPlanId(id);
    List<SeatingPlanResponseDto.Row> rowDtos = new ArrayList<>();
    List<SeatingPlanResponseDto.Sector> sectorDtos = new ArrayList<>();

    for (SeatingPlanSector sector : sectors) {
      List<SeatingPlanRow> rows =
          seatingPlanRowRepository.findBySeatingPlanIdAndSeatingPlanSector(id, sector.getNumber());
      for (SeatingPlanRow row : rows) {
        List<SeatingPlanResponseDto.Seat> seats =
            seatingPlanSeatRepository
                .findBySeatingPlanIdAndSeatingPlanSectorAndSeatingPlanRow(
                    id, sector.getNumber(), row.getNumber())
                .stream()
                .map(seatingPlanSeatMapper::seatingPlanSeatToSeatDto)
                .collect(Collectors.toList());
        rowDtos.add(new SeatingPlanResponseDto.Row(seats));
      }
      sectorDtos.add(
          new SeatingPlanResponseDto.Sector(
              sector.getNumber(),
              sector.getName(),
              sector.getColor(),
              sector.getCapacity(),
              sector.getType(),
              rowDtos));
      rowDtos = new ArrayList<>();
    }

    return new SeatingPlanResponseDto(
        seatingPlan.getName(), seatingPlan.getLocatedIn(), seatingPlan.getCapacity(), sectorDtos);
  }
}
