import {ReservationDto} from './reservation.dto';

export class ReservationInfoDto {
  constructor(
    public eventId: number,
    public showingId: number,
    public bookingInfo: ReservationDto
  ) {}
}
