import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {MatChipList} from '@angular/material/chips';
import {MatTable} from '@angular/material/table';
import {ArtistDto} from 'src/app/dtos/artist/artist.dto';
import {Showing} from 'src/app/dtos/event/showing.dto';
import {LocationDto} from 'src/app/dtos/location/location.dto';
import {ArtistService} from 'src/app/services/artist/artist.service';
import {EventService} from 'src/app/services/event/event.service';
import {LocationService} from 'src/app/services/location/location.service';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {SeatingPlanResponse} from 'src/app/dtos/seating-plan/seating-plan-response.dto';
import {LocationResponseDto} from 'src/app/dtos/location/location-response.dto';
import {
  SeatingPlan as VisualSeatingPlan,
  Sector as VisualSector,
  Row as VisualRow,
  Seat as VisualSeat
} from 'src/app/dtos/seating-plan/seating-plan-visual.dto';
import {Pricing} from 'src/app/dtos/event/pricing.dto';
import {EventDto} from 'src/app/dtos/event/event.dto';
import {Router} from '@angular/router';
import {NgxMaterialTimepickerTheme} from 'ngx-material-timepicker';
import {debounceTime, distinctUntilChanged, fromEvent} from 'rxjs';
import {EventCategoryDto} from '../../dtos/event/event-category.dto';


@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class CreateEventComponent implements OnInit, AfterViewInit {

  @ViewChild(MatTable) table: MatTable<any>;
  @ViewChild('artistInput') artistInput: ElementRef<HTMLInputElement>;
  @ViewChild('chipList') chipList: MatChipList;
  @ViewChild('uploadButton', { read: ElementRef }) uploadButton: ElementRef<HTMLElement>;

  timePickerTheme: NgxMaterialTimepickerTheme = {
    container: {
      buttonColor: '#6450e7'
    },
    dial: {
      dialBackgroundColor: '#6450e7',
    },
    clockFace: {
      clockHandColor: '#6450e7',
    }
  };

  headline = 'Create Event';
  expandedSeatingPlan: number | null;

  eventForm: FormGroup;
  enableSeatingPlan: boolean[] = [false];
  displayedColumns: string[] = ['startTime', 'endTime', 'location', 'seatingPlan'];
  availableLocations: LocationResponseDto[][] = [];
  availableSeatingPlans: SeatingPlanResponse[][] = [];
  availableCategories: EventCategoryDto[];
  availableArtists: ArtistDto[];
  addedArtists: ArtistDto[] = [];
  selectedSeatingPlansInformation: SeatingPlanResponse[] = [];
  selectedVisualSeatingPlans: VisualSeatingPlan[] = [];
  tableIndex = 0;
  showingArray: any[] = [this.tableIndex];

  constructor(
    private locationService: LocationService,
    private eventService: EventService,
    private artistService: ArtistService,
    private snackBarService: SnackBarService,
    private router: Router,
  ) {
    this.eventForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(2)]),
      category: new FormControl('', [Validators.required]),
      artist: new FormControl('', [Validators.required]),
      description: new FormControl('', [Validators.required, Validators.minLength(10)]),
      duration: new FormControl('2:00', [Validators.required]),
      startTimes: new FormArray([
        new FormControl('', [Validators.required]),
      ]),
      endTimes: new FormArray([
        new FormControl({value: '', disabled: true}, [Validators.required]),
      ]),
      locations: new FormArray([
        new FormControl('', [Validators.required]),
      ]),
      seatingPlans: new FormArray([
        new FormControl({value: '', disabled: true}, [Validators.required]),
      ]),
      pricings: new FormArray([
        new FormArray([])
      ]),
      imageRef: new FormControl('')
    });
  }

  @HostListener('keyup', ['$event.target'])
  addLocationListenerToNewRow(target) {
    const targetId = '' + target.id;
    if (this.tableIndex === 0 || !targetId.includes('locationInput')) {
      return;
    }
    fromEvent(target, 'keyup')
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => this.getLocations(+targetId.split('locationInput')[1]));
  }

  ngAfterViewInit(): void {
    fromEvent(document.querySelector('#locationInput0'), 'keyup')
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => this.getLocations(0));
  }

  ngOnInit(): void {
    this.getCategories();
    this.getArtists('');
    this.getLocations(0);
    fromEvent(document.querySelector('#artistInput'), 'keyup')
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((() => this.getArtists(this.fc('artist').value)));

    this.eventForm.setValidators(() => Validators.required(this.fa('seatingPlans').at(0)));
  }

  onStartTimeInput(index: number) {
    if (this.eventForm.value.startTimes.at(index)) {

      const startTime = new Date(this.eventForm.value.startTimes.at(index));
      if (startTime <= new Date()) {
        this.snackBarService.openWarningSnackBar('Your selected date is in the past.' +
        'Your date has been reset, please select a valid date.');
        this.fa('startTimes').controls[index].setValue('');
        this.fa('endTimes').controls[index].setValue('');
        return;
      }
      startTime.setSeconds(0,0);
      this.fa('startTimes').controls[index].setValue(new Date(startTime));
      const duration = new Date(0, 0, 0, this.eventForm.value.duration.split(':')[0], this.eventForm.value.duration.split(':')[1]);
      const endTime = startTime;
      endTime.setHours(endTime.getHours() + duration.getHours());
      endTime.setMinutes(endTime.getMinutes() + duration.getMinutes());
      this.fa('endTimes').controls[index].setValue(new Date(endTime));
    }
  }

  onDurationChange() {
    for (const i in this.showingArray) {
      if ({}.hasOwnProperty.call(this.showingArray, i)) {
        this.onStartTimeInput(+i);
        this.validateRow(+i);
      }
    }
  }

  getLocations(rowId: number) {
    const name = this.fa('locations').at(rowId).value;
    this.locationService.searchLocations(new LocationDto(name, null, null, null, null), 10).subscribe({
      next: (locations: LocationResponseDto[]) => {
        this.availableLocations[rowId] = locations;
      },
      error: error => {
        this.handleError(error);
      }
    });
  }

  getCategories() {
    this.eventService.getEventCategories().subscribe({
      next: categories => {
        this.availableCategories = categories;
      },
      error: error => {
        this.handleError(error);
      }
    });
  }

  getArtists(name: string) {
    this.artistService.getArtistsByNameFilter(name).subscribe({
      next: artists => {
        this.availableArtists = artists.filter(artist => this.addedArtists.find(addedArtist => addedArtist.id === artist.id) === undefined);
      },
      error: error => {
        this.handleError(error);
      }
    });
  }


  getSeatingPlans(rowId: number, locationId: number) {
    this.locationService.getSeatingPlanNamesByLocation(locationId).subscribe({
      next: seatingPlans => {
        this.availableSeatingPlans[rowId] = seatingPlans;
        const vSeatingPlan = new VisualSeatingPlan();
        vSeatingPlan.sectors = [];
        this.selectedVisualSeatingPlans[rowId] = vSeatingPlan;
        this.resetSeatingPlanInformation(rowId);
      },
      error: error => {
        this.handleError(error);
      }
    });
  }

  getSeatingPlanVisual(rowId: number) {
    const seatingPlanName = this.fa('seatingPlans').at(rowId).value;
    const seatingPlanId = this.availableSeatingPlans[rowId].find((seatingPlan) => seatingPlan.name === seatingPlanName).id;
    this.locationService.getSeatingPlanById(seatingPlanId).subscribe({
      next: seatingPlan => {
        this.selectedSeatingPlansInformation[rowId] = seatingPlan;
        this.selectedVisualSeatingPlans[rowId] = this.responseToVisual(seatingPlan);
        for (const _i of this.selectedSeatingPlansInformation[rowId].sectors) {
          (this.fa('pricings').at(rowId) as FormArray).push(new FormControl('', Validators.min(0)));
        }
      },
      error: error => {
        this.handleError(error);
      }
    });
  }

  resetSeatingPlanInformation(rowId: number) {
    this.fa('seatingPlans').at(rowId).setValue('');
    this.expandedSeatingPlan = null;
    this.enableSeatingPlan[rowId] = false;
  }

  responseToVisual(cSeatingPlan: SeatingPlanResponse): VisualSeatingPlan {
    const vSeatingPlan = new VisualSeatingPlan();
    vSeatingPlan.sectors = [];
    for (const i of cSeatingPlan.sectors) {
      const cSector = i;
      const vSector: VisualSector = new VisualSector();
      vSector.capacity = cSector.capacity;
      vSector.color = cSector.color;
      vSector.type = cSector.type;
      vSector.rows = [];
      for (const cRow of cSector.rows) {
        const vRow: VisualRow = new VisualRow();
        vRow.seats = [];
        for (const cSeat of cRow.seats) {
          const vSeat: VisualSeat = new VisualSeat();
          vSeat.visible = cSeat.enabled;
          vRow.seats.push(vSeat);
        }
        vSector.rows.push(vRow);
      }
      vSeatingPlan.sectors.push(vSector);
    }
    return vSeatingPlan;
  }

  selectSeatingPlan(rowId: number) {
    this.enableSeatingPlan[rowId] = true;
    this.getSeatingPlanVisual(rowId);
  }

  handleError(error) {
    if (error !== null) {
      this.snackBarService.openErrorSnackbar(error);
    }
  }

  addNewShow() {
    this.tableIndex++;
    this.fa('startTimes').push(new FormControl('', [Validators.required]));
    this.fa('endTimes').push(new FormControl({value: '', disabled: true}, [Validators.required]));
    this.fa('locations').push(new FormControl('', [Validators.required]));
    this.fa('seatingPlans').push(new FormControl({value: '', disabled: true}, [Validators.required]));
    this.fa('pricings').push(new FormArray([]));
    this.showingArray.push(this.tableIndex);
    this.enableSeatingPlan.push(false);
    this.table.renderRows();
    this.getLocations(this.showingArray.length-1);
  }

  removeShow(rowId: number) {
    this.fa('startTimes').removeAt(rowId);
    this.fa('endTimes').removeAt(rowId);
    this.fa('locations').removeAt(rowId);
    this.fa('seatingPlans').removeAt(rowId);
    this.fa('pricings').removeAt(rowId);
    this.showingArray.splice(rowId, 1);
    this.enableSeatingPlan.splice(rowId, 1);
    this.selectedSeatingPlansInformation.splice(rowId, 1);
    this.selectedVisualSeatingPlans.splice(rowId, 1);
    this.table.renderRows();
    for (const i in this.showingArray) {
      if ({}.hasOwnProperty.call(this.showingArray, i)) {
        this.validateRow(+i);
      }
    }
  }

  selectArtist(event: MatAutocompleteSelectedEvent): void {
    const value = event.option.viewValue;
    const artistToAdd = this.availableArtists.find((artist) => artist.artistName === value
      || artist.firstName + ' ' + artist.lastName === value);
    this.addedArtists.push(artistToAdd);
    this.availableArtists = this.availableArtists.filter((artist) => artist !== artistToAdd);
    this.artistInput.nativeElement.value = '';
    this.fc('artist').setValue(value);
  }

  onArtistChange() {
    if (this.addedArtists.length !== 0) {
      this.fc('artist').setErrors(null);
    }
  }

  removeArtist(artist) {
    const index = this.addedArtists.indexOf(artist);
    const artistToRemove = this.addedArtists[index];
    this.availableArtists.push(artistToRemove);
    if (index >= 0) {
      this.addedArtists.splice(index, 1);
    }
    if (this.addedArtists.length === 0) {
      this.fc('artist').setValue('');
    }
  }

  enableSeatingPlans(rowId: number) {
    this.fa('seatingPlans').controls[rowId].enable();
    const locationName = this.fa('locations').at(rowId).value;
    const locationId = this.availableLocations[rowId].find((location) => location.name === locationName).id;
    this.getSeatingPlans(rowId, locationId);
  }

  disableSeatingPlans(rowId: number) {
    this.fa('seatingPlans').controls[rowId].disable();
    this.resetSeatingPlanInformation(rowId);
  }

  fa(name: string) {
    return this.eventForm.get(name) as FormArray;
  }

  fc(name: string) {
    return this.eventForm.get(name) as FormControl;
  }

  redirectFileUploadClick() {
    const buttonElement: HTMLElement = this.uploadButton.nativeElement.children[1].children[0] as HTMLElement;
    buttonElement.click();
  }

  validateRow(rowId: number) {
    const startTime = this.fa('startTimes').at(rowId).value;
    const endTime = this.fa('endTimes').at(rowId).value;
    const seatingPlanName = this.fa('seatingPlans').at(rowId).value;
    if (startTime === '' || seatingPlanName === '') {
     return;
    }
    const seatingPlanId = this.availableSeatingPlans[rowId].find((seatingPlan) => seatingPlan.name === seatingPlanName).id;

    for (const i in this.showingArray) {
      if (this.availableSeatingPlans[+i]) {
        const seatingPlanCompare = this.availableSeatingPlans[+i].find((seatingPlan) => seatingPlan.name === seatingPlanName);
        if (seatingPlanCompare === undefined) {
          continue;
        }
        const seatingPlanIdCompare = seatingPlanCompare.id;
        if (+i !== rowId && seatingPlanId === seatingPlanIdCompare &&
          this.timesIntersect(startTime, endTime, this.fa('startTimes').at(+i).value, this.fa('endTimes').at(+i).value)) {
          this.fa('seatingPlans').at(rowId).setErrors({intersect: true});
          return;
        }
      }
    }

    this.eventService.isOccupied(seatingPlanId, startTime, endTime).subscribe({
      next: (occupied: boolean) => {
          if (occupied) {
            this.fa('seatingPlans').at(rowId).setErrors({occupied: true});
          } else if (!this.fa('seatingPlans').at(rowId).hasError('required')) {
            this.fa('seatingPlans').at(rowId).setErrors(null);
          }
      },
      error: error => {
          console.error(error);
      }
    });
  }

  timesIntersect(start1: Date, end1: Date, start2: Date, end2: Date): boolean {
    if (start1 <= start2 && end1 > start2 || start1 <= end2 && end1 > end2) {
return true;
}
    return false;
  }

  onSubmit() {
    if (this.eventForm.value.imageRef.size/1024/1024 >= 5) {
      this.snackBarService.openErrorSnackbar('The image you selected is too large. The maximum allowed file size is 5 MB.');
      return;
    }
    if (this.eventForm.valid === false) {
      this.snackBarService.openErrorSnackbar('At least one input is invalid. If you have not chosen ' +
        'a valid location from the drop down, please do so to view the available seating plans.');
      return;
    }
    const showings: Showing[] = [];
    const pricings: Pricing[][] = [];
    for (const i of this.showingArray) {
      pricings[i] = [];
      if (this.selectedSeatingPlansInformation[i]) {
        for (let j = 0; j < this.selectedSeatingPlansInformation[i].sectors.length; j++) {
          let pricing = (this.fa('pricings').at(i) as FormArray).at(j).value;
          if (!pricing) {
           pricing = 0;
          }
          pricings[i][j] = new Pricing(this.selectedSeatingPlansInformation[i].sectors[j].id, pricing);
        }
      }
      const startTime = this.fa('startTimes').at(i).value;
      const seatingPlanName = this.fa('seatingPlans').at(i).value;
      let seatingPlanId;
      if (seatingPlanName) {
        seatingPlanId = this.availableSeatingPlans[i].find((seatingPlan) => seatingPlan.name === seatingPlanName).id;
      }
      showings.push(new Showing(startTime, seatingPlanId, pricings[i]));
    }
    const title = this.eventForm.controls.name.value;
    const category = this.eventForm.controls.category.value;
    let categoryId;
    if (category) {
      categoryId = this.availableCategories.find((availableCategory) => availableCategory.displayName === category).id;
    }
    const artistIds = this.addedArtists.map((artist) => artist.id);
    const description = this.eventForm.controls.description.value;
    const duration = {
      hours: parseInt(this.eventForm.value.duration.split(':')[0], 10),
      minutes: parseInt(this.eventForm.value.duration.split(':')[1], 10)
    };
    const imageRef = this.eventForm.value.imageRef;
    const event = new EventDto(title, categoryId, artistIds, description, duration, showings, imageRef.name);
    this.eventService.createEvent(event).subscribe({
      next: returnedEvent => {
        if (imageRef !== '') {
          this.eventService.saveImage(returnedEvent.id, imageRef).subscribe({
            error: error => {
              this.snackBarService.openErrorSnackbar(error);
            }
          });
        }
        this.router.navigate(['administration']);
        this.snackBarService.openSuccessSnackbar('Event was successfully created!');
      },
      error: error => {
        this.handleError(error);
      }
    });
  }
}
