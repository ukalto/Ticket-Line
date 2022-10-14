import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { SnackBarService } from '../../services/snackbar/snack-bar.service';
import {
  SeatingPlan as VisualSeatingPlan,
  Sector as VisualSector,
  Row as VisualRow,
  Seat as VisualSeat,
  SeatSpecifier,
  NewSectorPosition
} from 'src/app/dtos/seating-plan/seating-plan-visual.dto';
import {
  SeatingPlan as CreationSeatingPlan,
  Sector as CreationSector,
  Row as CreationRow,
  Seat as CreationSeat,
  SectorType as CreationSectorType
} from 'src/app/dtos/seating-plan/seating-plan-creation.dto';
import { LocationDto } from '../../dtos/location/location.dto';
import { LocationResponseDto } from '../../dtos/location/location-response.dto';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { LocationService } from '../../services/location/location.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-seating-plan',
  templateUrl: './create-seating-plan.component.html',
  styleUrls: ['./create-seating-plan.component.scss']
})
export class CreateSeatingPlanComponent implements OnInit {
  planForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    location: ['', Validators.required],
  });

  maxRows = 20;
  minRows = 1;
  maxColumns = 14;
  minColumns = 1;
  minCapacity = 1;

  sectorTypeEnum = CreationSectorType;

  seatingPlan: CreationSeatingPlan = null;
  seatingPlanVisuals: VisualSeatingPlan = null;

  locationFilter: LocationDto = new LocationDto(
    null,
    null,
    null,
    null,
    null
  );

  selectedLocation: LocationResponseDto;
  filteredLocations: LocationResponseDto[];

  selectedSectorIndex = 0;
  sectorForm: FormGroup = this.fb.group({
    name: [''],
    color: [''],
    type: CreationSectorType,
    capacity: new FormControl(0, Validators.compose([
      Validators.min(this.minCapacity)
    ])),
    columns: new FormControl(0, Validators.compose([
      Validators.min(this.minColumns),
      Validators.max(this.maxColumns)
    ])),
    rows: new FormControl(0, Validators.compose([
      Validators.min(this.minRows),
      Validators.max(this.maxRows)
    ]))
  }, {
    updateOn: 'blur'
  });
  ignoreNextFormGroupChange = false;

  constructor(
    private fb: FormBuilder,
    private service: LocationService,
    private snackBarService: SnackBarService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.seatingPlan = this.getDefaultSeatingPlan();
    this.selectSector(0);
    this.updateSeatingPlanVisuals();

    this.sectorForm.valueChanges.subscribe((newValue) => this.onUpdateSector(newValue));
    this.planForm.valueChanges.pipe(
      distinctUntilChanged(),
      debounceTime(400)
    ).subscribe((newValue) => this.onUpdateSeatingPlanNameAndLocation(newValue));

    this.searchLocations();
  }

  create() {
    if (this.planForm.valid === false) {
      this.snackBarService.openErrorSnackbar(
        'Failed to create seating plan:'
        + ' name and or location are not valid.'
        + ' Please correct the input fields.'
      );
      return;
    }

    this.seatingPlan.capacity = this.getCurrentCapacity();
    // update referenced location to selected location
    if (this.selectedLocation) {
      this.seatingPlan.locatedIn = this.selectedLocation.id;
    }
    // send request
    this.service.createSeatingPlan(this.seatingPlan).subscribe({
      next: () => {
        this.router.navigate(['/administration']);
        this.snackBarService.openSuccessSnackbar('Successfully created a new seating plan!');
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  updateSeatingPlanVisuals() {
    this.seatingPlanVisuals = this.creationToVisual(this.seatingPlan);
  }

  onUpdateSeatingPlanNameAndLocation(newValue) {
    this.seatingPlan.name = newValue.name;
    this.locationFilter.name = newValue.location;

    if (!newValue.location && newValue.location !== '') {
      this.filteredLocations = [];
    } else {
      this.searchLocations();
    }
  }

  searchLocations() {
    this.service.searchLocations(this.locationFilter, 5).subscribe({
      next: res => {
        this.filteredLocations = res;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  onSelected(location: any) {
    this.selectedLocation = location;
  }

  displayWith(location: LocationResponseDto) {
    return location?.name;
  }

  toggleSeatActivation(seat: SeatSpecifier) {
    const currentlyEnabled = this.seatingPlan.sectors[seat.sector].rows[seat.row].seats[seat.seat].enabled;
    this.seatingPlan.sectors[seat.sector].capacity += currentlyEnabled ? -1 : 1;
    this.seatingPlan.sectors[seat.sector].rows[seat.row].seats[seat.seat].enabled = !currentlyEnabled;
    this.updateSeatingPlanVisuals();
  }

  addSector(newSectorPosition: NewSectorPosition) {
    const newSector: CreationSector = this.getDefaultSeatingSector();

    if (newSectorPosition.successor != null) {
      // insert
      this.seatingPlan.sectors.splice(newSectorPosition.successor, 0, newSector);
    } else if (newSectorPosition.predecessor != null) {
      // add as last element
      this.seatingPlan.sectors.push(newSector);
    }

    this.updateSeatingPlanVisuals();
  }

  deleteSelectedSector() {
    this.seatingPlan.sectors.splice(this.selectedSectorIndex, 1);
    this.selectSector(0);
    this.updateSeatingPlanVisuals();
  }

  selectSector(sectorIndex: number) {
    const sector: CreationSector = this.seatingPlan.sectors[sectorIndex];
    this.selectedSectorIndex = sectorIndex;
    const columns = sector.rows.length > 0 ? sector.rows[0].seats.length : 0;

    this.ignoreNextFormGroupChange = true;
    this.sectorForm.patchValue({
      name: sector.name,
      color: sector.color,
      type: sector.type,
      capacity: sector.capacity,
      columns,
      rows: sector.rows.length
    });

    this.updateSeatingPlanVisuals();
  }

  onUpdateSector(newValue) {
    if (this.ignoreNextFormGroupChange) {
      this.ignoreNextFormGroupChange = false;
      return;
    }

    if (this.sectorForm.invalid) {
      this.correctInputAndAdjustForm(newValue);
      return;
    }

    const oldSector: CreationSector = this.seatingPlan.sectors[this.selectedSectorIndex];
    let newSector: CreationSector;

    // switching sector type?
    const switchingType: boolean = newValue.type !== oldSector.type;
    if (switchingType) {
      this.generateNewSectorAt(this.selectedSectorIndex, oldSector.name, oldSector.color, newValue.type);
      this.selectSector(this.selectedSectorIndex);
      return;
    }

    // switching seat amount?
    if (newValue.type === CreationSectorType.seating) {
      const columnsChanged: boolean = newValue.columns !== oldSector.rows[0].seats.length;
      const rowsChanged: boolean = newValue.rows !== oldSector.rows.length;
      if (columnsChanged || rowsChanged) {
        // regenerate seats
        newSector = this.getNewSeatingSector(newValue.columns, newValue.rows);
        newSector.name = newValue.name;
        newSector.color = newValue.color;
        this.seatingPlan.sectors[this.selectedSectorIndex] = newSector;
        this.updateSeatingPlanVisuals();
        return;
      }
    }

    // just replacing values (no need to regenerate object)
    newSector = oldSector;
    newSector.name = newValue.name;
    newSector.color = newValue.color;
    newSector.capacity = newValue.capacity;
    this.seatingPlan.sectors[this.selectedSectorIndex] = newSector;
    this.updateSeatingPlanVisuals();
  }

  correctInputAndAdjustForm(newValue) {
    this.sectorForm.patchValue({
      name: newValue.name,
      color: newValue.color,
      type: newValue.type,
      capacity: Math.max(newValue.capacity, this.minCapacity),
      columns: Math.min(Math.max(newValue.columns, this.minColumns), this.maxColumns),
      rows: Math.min(Math.max(newValue.rows, this.minRows), this.maxRows)
    });
  }

  generateNewSectorAt(index: number, name: string, color: string, type: CreationSectorType) {
    let newSector: CreationSector;

    if (type === this.sectorTypeEnum.seating) {
      newSector = this.getDefaultSeatingSector();
    } else {
      newSector = this.getDefaultStandingSector();
    }
    newSector.name = name;
    newSector.color = color;
    this.seatingPlan.sectors[this.selectedSectorIndex] = newSector;
  }

  getTypeOfSelectedSector(): CreationSectorType {
    return this.seatingPlan.sectors[this.selectedSectorIndex].type;
  }

  getCurrentCapacity(): number {
    let capacity = 0;
    for (const sector of this.seatingPlan.sectors) {
      capacity += sector.capacity;
    }
    return capacity;
  }

  creationToVisual(cSeatingPlan: CreationSeatingPlan): VisualSeatingPlan {
    const vSeatingPlan = new VisualSeatingPlan();
    vSeatingPlan.sectors = [];
    for (let i = 0; i < cSeatingPlan.sectors.length; i++) {
      const cSector: CreationSector = cSeatingPlan.sectors[i];
      const vSector: VisualSector = new VisualSector();
      vSector.capacity = cSector.capacity;
      vSector.color = cSector.color;
      vSector.type = cSector.type;
      vSector.isSelected = i === this.selectedSectorIndex;
      vSector.rows = [];
      for (const cRow of cSector.rows) {
        const vRow: VisualRow = new VisualRow();
        vRow.seats = [];
        for (const cSeat of cRow.seats) {
          const vSeat: VisualSeat = new VisualSeat();
          vSeat.grayedOut = !cSeat.enabled;
          vSeat.visible = true;
          vRow.seats.push(vSeat);
        }
        vSector.rows.push(vRow);
      }
      vSeatingPlan.sectors.push(vSector);
    }
    return vSeatingPlan;
  }

  getDefaultSeatingSector(): CreationSector {
    return this.getNewSeatingSector(2, 2);
  }

  getDefaultStandingSector(): CreationSector {
    return this.getNewStandingSector(10);
  }

  getNewSeatingSector(columns: number, rows: number): CreationSector {
    const newSector: CreationSector = new CreationSector();
    newSector.name = 'New Sector';
    newSector.color = '#CCC2FF';
    newSector.capacity = columns * rows;
    newSector.type = CreationSectorType.seating;
    newSector.rows = [];
    for (let j = 0; j < rows; j++) {
      const row: CreationRow = new CreationRow();
      row.seats = [];
      for (let k = 0; k < columns; k++) {
        const seat: CreationSeat = new CreationSeat();
        seat.enabled = true;
        row.seats.push(seat);
      }
      newSector.rows.push(row);
    }
    return newSector;
  }

  getNewStandingSector(capacity: number): CreationSector {
    const newSector: CreationSector = new CreationSector();
    newSector.name = 'New Sector';
    newSector.color = '#CCC2FF';
    newSector.capacity = capacity;
    newSector.type = CreationSectorType.standing;
    newSector.rows = [];
    return newSector;
  }

  getDefaultSeatingPlan(): CreationSeatingPlan {
    return {
      name: '',
      locatedIn: -1,
      capacity: -1,
      sectors: [
        {
          name: 'Sector1',
          color: '#CCC2FF',
          type: CreationSectorType.seating,
          capacity: 4,
          rows: [
            {
              seats: [
                { enabled: true },
                { enabled: true }
              ]
            },
            {
              seats: [
                { enabled: true },
                { enabled: true }
              ]
            }
          ]
        },
        {
          name: 'Sector2',
          color: '#C2CFFF',
          type: CreationSectorType.standing,
          capacity: 10,
          rows: []
        }
      ]
    };
  }
}
