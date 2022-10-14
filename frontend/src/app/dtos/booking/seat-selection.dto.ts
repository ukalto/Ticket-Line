import {Seat, NonSeat} from './reservation.dto';
import {BookingType} from './booking-type';

export class SeatSelectionDto {
  constructor(
    public bookingType: BookingType,
    public seats: Seat[],
    public nonSeats: NonSeat[],
    public totalPrice: number
  ) {
  }
}

export class AreaBookingPriceInfo {
  constructor(
    public sectorName: string,
    public pricePerTicket: number,
    public ticketAmount: number,
    public totalPrice: number,
  ) {
  }
}
