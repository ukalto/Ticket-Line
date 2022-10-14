package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

public record EditUserResponseDto(
    Long id,
    String email,
    String cardOwner,
    String cardNumber,
    String cardExpirationDate,
    String cardCvv) {}
