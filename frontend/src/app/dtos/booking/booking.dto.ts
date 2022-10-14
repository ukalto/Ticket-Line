export class BookingDto {
  constructor(
    public bookingId: number,
    public bookedBy: number,
    public bookedAt: number,
    public eventShowingId: number,
    public isCancelled: boolean
  ) {
  }
}
