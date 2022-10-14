import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {MatStepper} from '@angular/material/stepper';
import {ActivatedRoute} from '@angular/router';
import {SeatSelectionDto} from 'src/app/dtos/booking/seat-selection.dto';
import {BookingType} from '../../dtos/booking/booking-type';
import {ReservationDto} from '../../dtos/booking/reservation.dto';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {EventService} from 'src/app/services/event/event.service';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';
import {Location} from '@angular/common';
import {EventDetailsService} from '../../services/event-details/event-details.service';
import { SeatSelectionComponent } from './seat-selection/seat-selection.component';

@Component({
  selector: 'app-booking-process',
  templateUrl: './booking-process.component.html',
  styleUrls: ['./booking-process.component.scss']
})
export class BookingProcessComponent implements OnInit, AfterViewInit {
  @ViewChild('stepper', { static: true }) stepper: MatStepper;
  @ViewChild(SeatSelectionComponent) seatSelection: SeatSelectionComponent;
  bookingInfo: ReservationDto;
  bookingType: BookingType;
  eventId: number;
  showingId: number;
  seatingPlanId: number;
  numberChecked = false;
  validatedId = false;
  bookingId: number;

  constructor(
    private location: Location,
    private _formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private snackBarService: SnackBarService,
     private authenticationService: AuthenticationService,
    private eventDetailsService: EventDetailsService,
    private eventService: EventService) { }

  ngOnInit() {
    this.readIdsFromUrl();

    if (!this.numberChecked) {
      return;
    }

    this.eventDetailsService.findById(this.eventId).subscribe({
      next: data => {
        if (data === null) {
          this.snackBarService.openErrorSnackbar('Given event does not exist!');
          this.location.back();
        } else {
          this.getSeatingPlanId();
        }
      },
      error: message => {
        this.snackBarService.openErrorSnackbar(message);
        this.location.back();
      }
    });
  }

  ngAfterViewInit() {
    if (this.validatedId) {
      this.stepper.steps.get(1).completed = false;
      this.stepper.steps.get(2).completed = false;
    }
  }

  readIdsFromUrl() {
    this.eventId = parseInt(this.route.snapshot.paramMap.get('eventId'), 10);
    if (isNaN(this.eventId)) {
      this.location.back();
      this.snackBarService.openErrorSnackbar('Given event id is not a number!');
      return;
    }

    this.showingId = parseInt(this.route.snapshot.paramMap.get('showingId'), 10);
    if (isNaN(this.showingId)) {
      this.location.back();
      this.snackBarService.openErrorSnackbar('Given showing id is not a number!');
      return;
    }

    this.numberChecked = true;
  }

  getSeatingPlanId() {
    this.eventService.getShowingByIdAndEventId(this.showingId, this.eventId).subscribe({
      next: data => {
        this.seatingPlanId = data.performedAt;
        this.validatedId = true;
        this.stepper.selectedIndex = 1;
      },
      error: message => {
        this.location.back();
          this.snackBarService.openErrorSnackbar(message);
      }
    });
  }

  submitSeatSelection(seatSelection: SeatSelectionDto) {
    this.bookingType = seatSelection.bookingType;
    this.bookingInfo = {
      bookedBy: this.authenticationService.getCurrentUserId(),
      seatingPlanId: this.seatingPlanId,
      cost: seatSelection.totalPrice,
      bookedSeats: seatSelection.seats,
      bookedNonSeats: seatSelection.nonSeats
    };

    this.stepper.steps.get(1).completed = true;
    this.stepper.next();
  }

  submitBooking(bookingId: number) {
    this.bookingId = bookingId;

    this.stepper.steps.get(2).completed = true;

    this.stepper.steps.get(1).editable = false;
    this.stepper.steps.get(2).editable = false;

    this.stepper.next();
  }

  submitBookingError(){
    this.stepper.steps.get(1).completed = false;
    this.stepper.steps.get(1).editable = true;
    this.stepper.steps.get(2).editable = true;
    this.stepper.selectedIndex = 1;

    this.seatSelection.reloadBookedSeats();
  }
}
