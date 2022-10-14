package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SectorType {
  standing("standing"),
  seating("seating");

  final String value;

  SectorType(String value) {
    this.value = value;
  }

  @JsonValue
  @Override
  public String toString() {
    return value;
  }

  @JsonCreator
  public static SectorType convertToEnum(String sectorType) {
    if (sectorType.equals("standing")) {
      return standing;
    }

    if (sectorType.equals("seating")) {
      return seating;
    }

    throw new ValidationException("cannot interpret " + sectorType + " as sectorId type");
  }
}
