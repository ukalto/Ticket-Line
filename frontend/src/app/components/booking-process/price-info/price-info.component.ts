import { Component, Input, OnInit } from '@angular/core';
import { ReservationDto } from 'src/app/dtos/booking/reservation.dto';
import { PriceAndSectorName } from 'src/app/dtos/event/price-and-sectorname.dto';
import { EventService } from 'src/app/services/event/event.service';
import { SnackBarService } from 'src/app/services/snackbar/snack-bar.service';
import { SeatingPlanResponse as SeatingPlan } from 'src/app/dtos/seating-plan/seating-plan-response.dto';
import { LocationService } from 'src/app/services/location/location.service';
import { NonSeatSpecifier, SeatSpecifier } from 'src/app/dtos/seating-plan/seating-plan-visual.dto';
import { BigNumber } from 'node_modules/bignumber.js/bignumber.mjs';

@Component({
  selector: 'app-price-info',
  templateUrl: './price-info.component.html',
  styleUrls: ['./price-info.component.scss']
})
export class PriceInfoComponent implements OnInit {
  @Input() bookingInfo: ReservationDto;
  @Input() showingId;

  sectorInfos: PriceAndSectorName[] = [];
  seatingPlanInput: SeatingPlan = null;

  ticketAmount = 0;
  categories: string[] = [];
  places: string[] = [];
  totalPrice = 0;

  constructor(private eventService: EventService, private snackBarService: SnackBarService, private locationService: LocationService) { }

  ngOnInit(): void {

    // fetch prices
    this.eventService.getShowingPrices(this.showingId).subscribe({
      next: sectorInfos => {
        this.sectorInfos = sectorInfos;

        // fetch seating plan
        this.locationService.getSeatingPlanById(this.bookingInfo.seatingPlanId).subscribe({
          next: seatingplan => {
            this.seatingPlanInput = seatingplan;

            this.assignAllValues();
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

  assignAllValues(){
    this.ticketAmount = this.getTicketAmount();
    this.categories = this.getCategories();
    this.places = this.getPlaces();
    this.totalPrice = this.getTotalPrice();
  }

  getTicketAmount(): number {
    let tickets = 0;
    this.bookingInfo.bookedSeats.forEach(seat => {
      tickets++;
    });
    this.bookingInfo.bookedNonSeats.forEach(sector => {
      tickets += Number(sector.amount);
    });
    return tickets;
  }

  getCategories(): string[] {
    if (!this.seatingPlanInput || !this.sectorInfos) {
      return [];
    }

    // assign amount of tickets to index in array with same index as sector
    const ticketAmountPerSector: number[] = Array<number>(this.seatingPlanInput.sectors.length).fill(0);
    this.bookingInfo.bookedSeats.forEach(element => {
      ticketAmountPerSector[element.sector]++;
    });
    this.bookingInfo.bookedNonSeats.forEach(element => {
      ticketAmountPerSector[element.sector] = element.amount;
    });

    // translate to strings
    const categoryStrings: string[] = [];
    for (let i = 0; i < ticketAmountPerSector.length; i++) {
      const ticketAmount = ticketAmountPerSector[i];
      if (ticketAmount > 0) {
        categoryStrings.push(ticketAmount + 'x' + this.sectorInfos[i].price + '€ ' + this.sectorInfos[i].name);
      }
    }
    return categoryStrings;
  }

  getPlaces(): string[] {
    const places: string[] = [];
    this.bookingInfo.bookedSeats.forEach(seat => {
      const s = this.seatIndicesStartWith1(seat);
      places.push('Sector: ' + s.sector + ' Row: ' + s.row + ' Seat: ' + s.seat);
    });
    this.bookingInfo.bookedNonSeats.forEach(sector => {
      const s = this.sectoIndicesStartWith1(sector);
      places.push(sector.amount + 'xStanding Sector ' + s.sector);
    });

    return places;
  }

  seatIndicesStartWith1(origin: SeatSpecifier): SeatSpecifier {
    const s = new SeatSpecifier();
    s.seat = origin.seat + 1;
    s.row = origin.row + 1;
    s.sector = origin.sector + 1;
    return s;
  }
  sectoIndicesStartWith1(origin: NonSeatSpecifier): NonSeatSpecifier {
    const s = new NonSeatSpecifier();
    s.sector = origin.sector + 1;
    return s;
  }

  getTotalPrice(): number {
    if (this.sectorInfos.length <= 0) {
      return 0;
    }

    let totalPrice: BigNumber = new BigNumber(0.0);
    this.bookingInfo.bookedSeats.forEach(seat => {
      totalPrice = totalPrice.plus(this.sectorInfos[seat.sector].price);
    });
    this.bookingInfo.bookedNonSeats.forEach(sector => {
      totalPrice = totalPrice.plus(this.sectorInfos[sector.sector].price * sector.amount);
    });
    return totalPrice;
  }
}
