package at.ac.tuwien.sepm.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.validation.ValidationException;

public enum InvoiceType {
  purchase("purchase"),
  cancellation("cancellation");

  final String value;

  InvoiceType(String value) {
    this.value = value;
  }

  @JsonValue
  @Override
  public String toString() {
    return value;
  }

  @JsonCreator
  public static InvoiceType convertToEnum(String invoiceType) {
    if (invoiceType.toLowerCase().equals("purchase")) {
      return purchase;
    }

    if (invoiceType.toLowerCase().equals("cancellation")) {
      return cancellation;
    }

    throw new ValidationException("cannot interpret " + invoiceType + " as invoice type");
  }
}
