import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BookingType } from 'src/app/dtos/booking/booking-type';
import { NonSeat, Seat } from 'src/app/dtos/booking/reservation.dto';
import { AreaBookingPriceInfo, SeatSelectionDto } from 'src/app/dtos/booking/seat-selection.dto';
import {
  NonSeatSpecifier,
  Row as VRow,
  Seat as VSeat,
  SeatingPlan as VisualSeatingPlan,
  SeatSpecifier,
  Sector as VSector,
  SectorType
} from 'src/app/dtos/seating-plan/seating-plan-visual.dto';
import { SeatingPlanResponse as SelectionSeatingPlan } from 'src/app/dtos/seating-plan/seating-plan-response.dto';
import { EventService } from 'src/app/services/event/event.service';
import { LocationService } from 'src/app/services/location/location.service';
import { SnackBarService } from 'src/app/services/snackbar/snack-bar.service';
import { PriceAndSectorName } from 'src/app/dtos/event/price-and-sectorname.dto';
import { BigNumber } from 'node_modules/bignumber.js/bignumber.mjs';

@Component({
  selector: 'app-seat-selection',
  templateUrl: './seat-selection.component.html',
  styleUrls: ['./seat-selection.component.scss']
})
export class SeatSelectionComponent implements OnInit {
  @Input() eventId: number;
  @Input() showingId: number;
  @Input() preLoadSeats: Seat[] = [];
  @Input() preLoadNonSeats: NonSeat[] = [];
  @Input() preLoadNonSeatsSpecifiers: NonSeatSpecifier[] = null;
  @Input() purchaseReservation = false;
  @Output() submitEvent = new EventEmitter<SeatSelectionDto>();
  bookingTypeEnum = BookingType;
  seatingPlanInput: SelectionSeatingPlan = null;
  bookedSeats: SeatSpecifier[] = [];
  bookedNonSeats: NonSeatSpecifier[] = [];
  sectorPrices: PriceAndSectorName[] = [];
  visualSeatingPlan: VisualSeatingPlan = null;
  selectedSeats: Seat[] = [];
  selectedNonSeats: NonSeat[] = [];
  areaPriceInfos: AreaBookingPriceInfo[] = [];
  totalPrice: number;
  backButton: string;

  constructor(private eventService: EventService, private locationService: LocationService, private snackBarService: SnackBarService) { }

  ngOnInit(): void {
    this.fetchSeatingPlanInfos();
    this.fetchPricing();
  }

  selectPreSelectedTickets() {
    this.preLoadSeats.forEach(seat => {
      const seatSpecifier = new SeatSpecifier();
      seatSpecifier.sector = seat.sector - 1;
      seatSpecifier.row = seat.row - 1;
      seatSpecifier.seat = seat.seat - 1;
      this.onClickSeat(seatSpecifier);
    });
    if (this.preLoadNonSeats.length > 0) {
      this.preLoadNonSeatsSpecifiers = [];
      this.preLoadNonSeats.forEach(nonSeat => {
        const nonSeatSpecifier = new NonSeatSpecifier();
        nonSeatSpecifier.sector = nonSeat.sector - 1;
        nonSeatSpecifier.amount = nonSeat.amount;
        this.preLoadNonSeatsSpecifiers.push(nonSeatSpecifier);
        this.onChangeStandingAreaTickets(nonSeatSpecifier);
      });
    }
  }

  fetchSeatingPlanInfos() {
    // get seating plan id
    this.eventService.getShowing(this.showingId).subscribe({
      next: showing => {

        // seating plan
        this.locationService.getSeatingPlanById(showing.performedAt).subscribe({
          next: planData => {
            this.seatingPlanInput = planData;

            // booked seats
            this.eventService.getBookedSeats(this.showingId).subscribe({
              next: bookedSeatsData => {
                this.bookedSeats = bookedSeatsData;

                // booked non-seats
                this.eventService.getBookedNonSeats(this.showingId).subscribe({
                  next: bookedNonSeatsData => {
                    this.bookedNonSeats = bookedNonSeatsData;

                    // update visuals
                    this.updateSeatingPlanVisuals();
                    this.selectPreSelectedTickets();
                  },
                  error: error => {
                    this.snackBarService.openErrorSnackbar(error);
                  }
                });
              },
              error: error => {
                this.snackBarService.openErrorSnackbar(error);
              }
            });
          },
          error: error => {
            this.snackBarService.openErrorSnackbar(error);
          }
        });
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  fetchPricing() {
    this.eventService.getShowingPrices(this.showingId).subscribe({
      next: data => {
        this.sectorPrices = data;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  onClickSeat(seatSpecifier: SeatSpecifier) {
    const seat: Seat = new Seat(seatSpecifier.seat, seatSpecifier.row, seatSpecifier.sector);
    const seatIsInSelectedSeats = this.getSelectedSeatIndex(seat) > -1;
    if (seatIsInSelectedSeats) {
      // remove from list
      const seatArrayIndex = this.getSelectedSeatIndex(seat);
      this.selectedSeats.splice(seatArrayIndex, 1);
    } else {
      // add to list
      this.selectedSeats.push(seat);
    }
    this.visualSeatingPlan
      .sectors[seatSpecifier.sector]
      .rows[seatSpecifier.row]
      .seats[seatSpecifier.seat]
      .isSelected = !seatIsInSelectedSeats;
    this.updateSectorPricingStrings();
  }

  getSelectedSeatIndex(seat: Seat): number {
    for (let i = 0; i < this.selectedSeats.length; i++) {
      const element = this.selectedSeats[i];
      if (element.seat === seat.seat && element.row === seat.row && element.sector === seat.sector) {
        return i;
      }
    }
    return -1;
  }

  onChangeStandingAreaTickets(changeInfo: NonSeatSpecifier) {
    const nonSeat: NonSeat = new NonSeat(changeInfo.sector, changeInfo.amount);
    const arrayIndex = this.getSelectedNonSeatIndex(nonSeat);
    const nonSeatIsInSelectedSeats = arrayIndex > -1;

    if (!nonSeatIsInSelectedSeats) {
      // add to list
      this.selectedNonSeats.push(nonSeat);
      this.updateSectorPricingStrings();
      return;
    }

    if (nonSeat.amount === null || nonSeat.amount <= 0) {
      // remove from list
      this.selectedNonSeats.splice(arrayIndex, 1);
    } else {
      // change value of existing entry
      this.selectedNonSeats[arrayIndex].amount = nonSeat.amount;
    }
    this.updateSectorPricingStrings();
  }

  getSelectedNonSeatIndex(nonSeat: NonSeat): number {
    for (let i = 0; i < this.selectedNonSeats.length; i++) {
      const element = this.selectedNonSeats[i];
      if (element.sector === nonSeat.sector) {
        return i;
      }
    }
    return -1;
  }

  getEventUrlPath(): string {
    return '/events/' + this.eventId;
  }

  choseSeats(): boolean {
    return this.selectedSeats.length > 0 || this.selectedNonSeats.length > 0;
  }

  updateSectorPricingStrings() {
    const areaPriceInfos: AreaBookingPriceInfo[] = [];

    for (let i = 0; i < this.visualSeatingPlan.sectors.length; i++) {
      const sector = this.visualSeatingPlan.sectors[i];

      // Find out the amount of tickets for this sector
      let amountOfTickets = 0;
      if (sector.type === SectorType.seating) {
        // count seats in that area
        this.selectedSeats.forEach(element => {
          if (element.sector === i) {
            amountOfTickets++;
          }
        });
      } else if (sector.type === SectorType.standing) {
        // find right sector and take its ticket amount
        this.selectedNonSeats.forEach(element => {
          if (element.sector === i) {
            amountOfTickets = element.amount;
          }
        });
      }

      if (amountOfTickets > 0) {
        // add to array
        const sectorPrice: BigNumber = new BigNumber(this.sectorPrices[i].price);

        areaPriceInfos.push(new AreaBookingPriceInfo(
          this.seatingPlanInput.sectors[i].name,
          sectorPrice,
          amountOfTickets,
          sectorPrice.multipliedBy(amountOfTickets)
        ));
      }
    }

    this.areaPriceInfos = areaPriceInfos;
  }

  getTotalPrice(): number {
    if (this.sectorPrices.length <= 0) {
      return;
    }

    let totalPrice: BigNumber = new BigNumber(0.0);
    this.selectedSeats.forEach(seat => {
      totalPrice = totalPrice.plus(this.sectorPrices[seat.sector].price);
    });
    this.selectedNonSeats.forEach(sector => {
      totalPrice = totalPrice.plus(this.sectorPrices[sector.sector].price * sector.amount);
    });
    return totalPrice;
  }

  updateSeatingPlanVisuals() {
    const vSeatingPlan = new VisualSeatingPlan();
    vSeatingPlan.sectors = [];
    this.seatingPlanInput.sectors.forEach(cSector => {
      const vSector: VSector = new VSector();
      vSector.capacity = cSector.capacity;
      vSector.vacancies = cSector.capacity;
      vSector.color = cSector.color;
      vSector.type = cSector.type;
      vSector.rows = [];
      for (const cRow of cSector.rows) {
        const vRow: VRow = new VRow();
        vRow.seats = [];
        for (const cSeat of cRow.seats) {
          const vSeat: VSeat = new VSeat();
          vSeat.visible = cSeat.enabled;
          vRow.seats.push(vSeat);
        }
        vSector.rows.push(vRow);
      }
      vSeatingPlan.sectors.push(vSector);
    });

    // remove all reserved places from the selectable places
    this.bookedSeats.forEach(seat => {
      vSeatingPlan.sectors[seat.sector - 1].rows[seat.row - 1].seats[seat.seat - 1].grayedOut = true;
    });
    this.bookedNonSeats.forEach(nonSeat => {
      const vacancies = vSeatingPlan.sectors[nonSeat.sector - 1].vacancies;
      vSeatingPlan.sectors[nonSeat.sector - 1].vacancies = vacancies - nonSeat.amount;
    });

    // readd all places reserved by this person as selectable
    this.preLoadSeats.forEach(seat => {
      vSeatingPlan.sectors[seat.sector - 1].rows[seat.row - 1].seats[seat.seat - 1].grayedOut = false;
    });
    this.preLoadNonSeats.forEach(nonSeat => {
      const currentVacancies = vSeatingPlan.sectors[nonSeat.sector - 1].vacancies;
      vSeatingPlan.sectors[nonSeat.sector - 1].vacancies = currentVacancies + nonSeat.amount;
    });

    this.visualSeatingPlan = vSeatingPlan;
  }

  onSubmit(bookingType: BookingType) {
    const seatSelection: SeatSelectionDto = new SeatSelectionDto(
      bookingType,
      this.selectedSeats,
      this.selectedNonSeats,
      this.getTotalPrice());
    this.submitEvent.emit(seatSelection);
  }

  reloadBookedSeats(){
    const bookedSeats: SeatSpecifier[] = null;
    const bookedNonSeats: NonSeatSpecifier[] = null;
    // load bookings
    // booked seats
    this.eventService.getBookedSeats(this.showingId).subscribe({
      next: bookedSeatsData => {
        this.bookedSeats = bookedSeatsData;

        // booked non-seats
        this.eventService.getBookedNonSeats(this.showingId).subscribe({
          next: bookedNonSeatsData => {
            this.bookedNonSeats = bookedNonSeatsData;

            // update visuals
            this.updateSeatingPlanVisuals();

            const tempSelectedSeats = this.selectedSeats;
            const tempSelectedNonSeats = this.selectedNonSeats;
            // unselect all seats and non seats
            this.selectedSeats = [];
            this.selectedNonSeats = [];

            // reselect all allowed seats
            tempSelectedSeats.forEach(seat => {
              if(!this.visualSeatingPlan.sectors[seat.sector].rows[seat.row].seats[seat.seat].grayedOut){
                const seatSpecifier = new SeatSpecifier(seat.sector, seat.row, seat.seat);
                this.onClickSeat(seatSpecifier);
              }
            });
            // reselect all allowed non seats
            this.preLoadNonSeatsSpecifiers = [];
            tempSelectedNonSeats.forEach(nonSeat => {
              const vacancies: number = this.visualSeatingPlan.sectors[nonSeat.sector].vacancies;
              let ticketAmount: number = nonSeat.amount;
              if(ticketAmount > vacancies){
                ticketAmount = vacancies;
              }
              const nonSeatSpecifier = new NonSeatSpecifier(nonSeat.sector, ticketAmount);
              this.preLoadNonSeatsSpecifiers.push(nonSeatSpecifier);
              this.onChangeStandingAreaTickets(nonSeatSpecifier);
            });
          },
          error: error => {
            this.snackBarService.openErrorSnackbar(error);
          }
        });
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }
}
