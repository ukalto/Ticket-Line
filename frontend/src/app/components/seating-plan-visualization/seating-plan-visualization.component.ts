import { Component, OnInit, Input, Output, EventEmitter, ViewChildren, QueryList } from '@angular/core';
import {
  SeatingPlan,
  SectorType,
  NewSectorPosition,
  SeatSpecifier,
  NonSeatSpecifier
} from 'src/app/dtos/seating-plan/seating-plan-visual.dto';
import { MatInput } from '@angular/material/input';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-seating-plan-visualization',
  templateUrl: './seating-plan-visualization.component.html',
  styleUrls: ['./seating-plan-visualization.component.scss']
})
export class SeatingPlanVisualizationComponent implements OnInit {

  @Input() data: SeatingPlan = null;
  @Input() enableSeatClicking = false;
  @Input() enableSectorClicking = false;
  @Input() enableAddSectorHandles = false;
  @Input() enableNonSeatInputFields = false;
  @Input() graySeatsAreClickableToo = true;
  @Input() scaleDown = false;
  @Input() preSelectedNonSeats: NonSeatSpecifier[] = null;

  @Output() newSectorEvent = new EventEmitter<NewSectorPosition>();
  @Output() clickedSectorEvent = new EventEmitter<number>();
  @Output() clickedSeatEvent = new EventEmitter<SeatSpecifier>();
  @Output() changedNonSeatInput = new EventEmitter<NonSeatSpecifier>();

  @ViewChildren(MatInput) standingAreaInputs!: QueryList<MatInput>;

  sectorType = SectorType;
  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    if (this.data == null) {
      this.data = this.getDefaultSeatingPlan();
    }
  }

  sectorIndexToPreSelectedNonSeatsIndex(sectorIndex: number): number {
    for (let i = 0; i < this.preSelectedNonSeats.length; i++) {
      if(sectorIndex === this.preSelectedNonSeats[i].sector){
        return i;
      }
    }
    return null;
  }

  onChangeStandingAreaTicketAmount(sectorId: number, changeEvent) {
    // restrict ticket amount
    let ticketAmount = changeEvent.target.value;
    const vacancies = this.data.sectors[sectorId].vacancies;

    if (ticketAmount > vacancies) {
      ticketAmount = vacancies;
    } else if (ticketAmount <= 0) {
      ticketAmount = null;
    }
    changeEvent.target.value = ticketAmount;

    // emit change event
    const nonSeatBookingSpecifier: NonSeatSpecifier = new NonSeatSpecifier();
    nonSeatBookingSpecifier.sector = sectorId;
    nonSeatBookingSpecifier.amount = ticketAmount;
    this.changedNonSeatInput.emit(nonSeatBookingSpecifier);
  }

  getAmountOfStandingSectors(): number {
    let amount = 0;
    this.data.sectors.forEach(element => {
      if (element.type === SectorType.standing) {
        amount++;
      }
    });
    return amount;
  }

  addSector(predecessor: number, successor: number) {
    if (!this.enableAddSectorHandles) {
      return;
    }

    const position: NewSectorPosition = { predecessor, successor };
    this.newSectorEvent.emit(position);
  }

  clickSector(sectorIndex: number) {
    if (!this.enableSectorClicking) {
      return;
    }

    this.clickedSectorEvent.emit(sectorIndex);
  }

  clickSeat(sectorIndex: number, rowIndex: number, seatIndex: number) {
    if (!this.seatIsClickable(sectorIndex, rowIndex, seatIndex)) {
      return;
    }

    const seatPosition: SeatSpecifier = { sector: sectorIndex, row: rowIndex, seat: seatIndex };
    this.clickedSeatEvent.emit(seatPosition);
  }

  seatIsClickable(sectorIndex: number, rowIndex: number, seatIndex: number): boolean {
    const seatIsGrayedOut = this.data.sectors[sectorIndex].rows[rowIndex].seats[seatIndex].grayedOut;
    const seatIsVisible = this.data.sectors[sectorIndex].rows[rowIndex].seats[seatIndex].visible;
    return this.enableSeatClicking &&
      (!seatIsGrayedOut || (seatIsGrayedOut && this.graySeatsAreClickableToo)) &&
      (seatIsVisible || (!seatIsVisible && this.graySeatsAreClickableToo));
  }

  getDefaultSeatingPlan(): SeatingPlan {
    return {
      sectors: [
        {
          color: '#CCC2FF',
          type: SectorType.seating,
          capacity: 0,
          isSelected: false,
          rows: [
            {
              seats: [
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
              ]
            },
            {
              seats: [
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
              ]
            }
          ]
        },
        {
          color: '#C2CFFF',
          type: SectorType.standing,
          capacity: 10,
          vacancies: 5,
          isSelected: false,
          rows: []
        },
        {
          color: '#DDFFC2',
          type: SectorType.seating,
          capacity: 0,
          isSelected: false,
          rows: [
            {
              seats: [
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
              ]
            },
            {
              seats: [
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
              ]
            },
            {
              seats: [
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
                { visible: true, grayedOut: false, isSelected: false },
              ]
            }
          ]
        }
      ]
    };
  }

}
