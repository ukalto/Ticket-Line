package at.ac.tuwien.sepm.groupphase.backend.repository;

public interface BookedSeat {
  Long getSector();

  Long getSeatingPlanRow(); // name "row" causes sql syntax errors

  Long getSeat();
}
