export class ReservationDto {
  constructor(
    public bookedBy: number,
    public seatingPlanId: number,
    public cost: number,
    public bookedSeats: Seat[],
    public bookedNonSeats: NonSeat[]
  ) {
  }
}

export class Seat {
  constructor(
    public seat: number,
    public row: number,
    public sector: number
  ) {
  }
}

export class NonSeat {
  constructor(
    public sector: number,
    public amount: number
  ) {
  }
}
