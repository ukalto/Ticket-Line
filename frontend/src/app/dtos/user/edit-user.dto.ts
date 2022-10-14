export class EditUserDto {
  constructor(
    public id: number,
    public email: string,
    public cardOwner: string,
    public cardNumber: string,
    public cardExpirationDate: string,
    public cardCvv: string
  ) {
  }
}
