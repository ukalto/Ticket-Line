import {PaymentInfoDto} from './payment-info.dto';

export class ReservationPurchaseDto {
  constructor(
    public seatingPlanId: number,
    public bookedSeats: Seat[],
    public bookedNonSeats: NonSeat[],
    public cost: number,
    public paymentInfoDto: PaymentInfoDto
  ) {}
}
  export class Seat {
  constructor(
    public seat: number,
    public row: number,
    public sector: number
  ) {}
}

export class NonSeat {
  constructor(
    public sector: number,
    public amount: number
  ) {}
}
