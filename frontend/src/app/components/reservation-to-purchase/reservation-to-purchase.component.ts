import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {BookingService} from '../../services/booking/booking.service';
import {ReservationInfoDto} from '../../dtos/booking/reservation-info.dto';
import {BookingType} from '../../dtos/booking/booking-type';
import {SeatSelectionDto} from '../../dtos/booking/seat-selection.dto';

@Component({
  selector: 'app-reservation-to-purchase',
  templateUrl: './reservation-to-purchase.component.html',
  styleUrls: ['./reservation-to-purchase.component.scss']
})
export class ReservationToPurchaseComponent implements OnInit {
  reservationInfo: ReservationInfoDto;
  infoPresent = false;
  purchaseType = BookingType.purchase;

  seatsSelected = false;
  purchaseConfirmed = false;
  bookingId: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookingService: BookingService,
    private snackBarService: SnackBarService
  ) { }

  ngOnInit(): void {
    this.bookingId = parseInt(this.route.snapshot.paramMap.get('bookingId'), 10);
    if (isNaN(this.bookingId)) {
      this.snackBarService.openErrorSnackbar('Given booking id is not a number!');
      this.router.navigate(['booking-overview/']);
    }

    this.bookingService.getReservationInfo(this.bookingId).subscribe({
      next: (data) => {
        this.reservationInfo = data;
        this.infoPresent = true;
      },
      error: (err) => {
        this.snackBarService.openErrorSnackbar(err);
        this.router.navigate(['booking-overview']);
      }
    });
  }

  submitSeats(seatSelection: SeatSelectionDto) {
    this.seatsSelected = true;

    this.reservationInfo.bookingInfo = {
      bookedBy: this.reservationInfo.bookingInfo.bookedBy,
      seatingPlanId: this.reservationInfo.bookingInfo.seatingPlanId,
      cost: seatSelection.totalPrice,
      bookedSeats: seatSelection.seats,
      bookedNonSeats: seatSelection.nonSeats,
    };
  }

  submitPurchase() {
    this.purchaseConfirmed = true;
  }
}
