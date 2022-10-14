export class PaymentInfoDto {
  constructor(
    public cardOwner: string,
    public cardNumber: string,
    public cardExpirationDate: string,
    public cardCvv: string
  ) {
  }
}
