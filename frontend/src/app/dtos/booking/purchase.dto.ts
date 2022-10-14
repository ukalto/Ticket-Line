import {ReservationDto} from './reservation.dto';

export class PurchaseDto {
  constructor(
    public bookingInfo: ReservationDto,
    public cardOwner: string,
    public cardNumber: string,
    public cardExpirationDate: string,
    public cardCvv: string
  ) {
  }
}
