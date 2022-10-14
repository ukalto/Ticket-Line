import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NonSeat, ReservationDto, Seat } from '../../../dtos/booking/reservation.dto';
import { BookingType } from '../../../dtos/booking/booking-type';
import { BookingService } from '../../../services/booking/booking.service';
import { SnackBarService } from '../../../services/snackbar/snack-bar.service';
import { BookingDto } from 'src/app/dtos/booking/booking.dto';

@Component({
  selector: 'app-conclude-booking',
  templateUrl: './conclude-booking.component.html',
  styleUrls: ['./conclude-booking.component.scss']
})
export class ConcludeBookingComponent implements OnInit {
  @Input() bookingInfo: ReservationDto;
  @Input() bookingType: BookingType;
  @Input() eventId: number;
  @Input() showingId: number;

  @Output() submitBooking = new EventEmitter<number>();
  @Output() submitError = new EventEmitter();

  title: string;
  bookingDto: BookingDto;

  constructor(private bookingService: BookingService,
    private snackBarService: SnackBarService) { }

  ngOnInit(): void {
    if (this.bookingType === BookingType.purchase) {
      this.title = 'Pay for Tickets';
    } else {
      this.title = 'Reserve Tickets';
    }
  }

  confirmReservation() {
    const adjustedBookingInfo = this.adjustIndicesOfBookingInfo();

    this.bookingService.reserve(adjustedBookingInfo, this.showingId).subscribe({
      next: (data) => {
        this.bookingDto = data;

        this.submit(this.bookingDto.bookingId);
      },
      error: (message) => {
        this.snackBarService.openErrorSnackbar(message);
        this.submitError.emit();
      }
    });
  }

  adjustIndicesOfBookingInfo(): ReservationDto {
    const adjustedBookingInfo = new ReservationDto(this.bookingInfo.bookedBy, this.bookingInfo.seatingPlanId,
      this.bookingInfo.cost, [], []);
    this.bookingInfo.bookedSeats.forEach(element => {
      adjustedBookingInfo.bookedSeats.push(new Seat(element.seat + 1, element.row + 1, element.sector + 1));
    });
    this.bookingInfo.bookedNonSeats.forEach(element => {
      adjustedBookingInfo.bookedNonSeats.push(new NonSeat(element.sector + 1, element.amount));
    });
    return adjustedBookingInfo;
  }

  submit(id: number) {
    this.submitBooking.emit(id);
  }

  submitWithError() {
    this.submitError.emit();
  }
}
