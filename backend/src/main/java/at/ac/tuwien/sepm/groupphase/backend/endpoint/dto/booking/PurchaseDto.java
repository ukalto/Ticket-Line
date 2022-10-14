package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.booking;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record PurchaseDto(
    @NotNull ReservationDto bookingInfo,
    @Pattern(
            regexp =
                "^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9]"
                    + "[0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|"
                    + "(?:2131|1800|35\\d{3})\\d{11})$",
            message = "must be a valid card number!")
        String cardNumber,
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "format is not valid!")
        String cardExpirationDate,
    @Pattern(regexp = "^([0-9]{3,4})$", message = "format is not valid!") String cardCvv,
    String cardOwner) {}
