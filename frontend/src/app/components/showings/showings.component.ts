import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {EventDetailsShowsDto} from '../../dtos/event/event-details-shows.dto';
import {EventDetailsService} from '../../services/event-details/event-details.service';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {EventShowingFilterDto} from '../../dtos/event/event-showing-filter.dto';
import {FormControl, FormGroup} from '@angular/forms';
import {NgxMaterialTimepickerTheme} from 'ngx-material-timepicker';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-shows',
  templateUrl: './showings.component.html',
  styleUrls: ['./showings.component.scss']
})
export class ShowingsComponent implements OnInit, AfterViewInit {

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  dataSource: MatTableDataSource<EventDetailsShowsDto>;
  pageIndex = 0;
  pageSize = 10;
  pageSizeOptions = [5, 10, 25, 100];
  displayedColumns: string[] = ['date', 'eventTitle', 'time', 'location', 'room', 'pricing', 'bookNow'];

  showingForm: FormGroup;
  paramsEmpty = true;
  currentDate: Date =  new Date(Date.now());
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

  showingFilter = new EventShowingFilterDto(
    null,
    null,
    null,
    null,
    null,
    null,
    null);

  constructor(
    private eventDetailsService: EventDetailsService,
    private snackBarService: SnackBarService,
    public authenticationService: AuthenticationService,
    private route: ActivatedRoute) {

    this.showingForm = new FormGroup({
      eventTitle: new FormControl(''),
      locationName: new FormControl(''),
      date: new FormControl(null),
      startTime: new FormControl(''),
      endTime: new FormControl(''),
      minPrice: new FormControl(''),
      maxPrice: new FormControl('')
    });
    this.dataSource = new MatTableDataSource<EventDetailsShowsDto>([]);
  }

  ngOnInit(): void {
    this.checkQueryParams();
    this.route.queryParams.subscribe(params => {
      if (params.eventTitle) {
        this.showingForm.patchValue({eventTitle: params.eventTitle});
      }
      if (params.locationName) {
        this.showingForm.patchValue({locationName: params.locationName});
      }
      if (params.date) {
        this.showingForm.patchValue({date: params.date});
      }
      if (params.startTime) {
        this.showingForm.patchValue({startTime: params.startTime});
      }
      if (params.endTime) {
        this.showingForm.patchValue({endTime: params.endTime});
      }
      if (params.minPrice) {
        this.showingForm.patchValue({minPrice: params.minPrice});
      }
      if (params.maxPrice) {
        this.showingForm.patchValue({maxPrice: params.maxPrice});
      }

      this.filterShows();
    });

  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  resetForm() {
    this.showingForm.patchValue({eventTitle: ''});
    this.showingForm.patchValue({locationName: ''});
    this.showingForm.patchValue({date: null});
    this.showingForm.patchValue({startTime: ''});
    this.showingForm.patchValue({endTime: ''});
    this.showingForm.patchValue({minPrice: ''});
    this.showingForm.patchValue({maxPrice: ''});

    this.filterShows();
  }

  changePaginator(event) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.filterShows();
  }

  filterShows(): void {
    this.updateFilter();

    this.eventDetailsService.filterShowings(this.showingFilter, this.pageIndex, this.pageSize).subscribe({
      next: (data) => {
        if (data != null) {
          const emptySize = data.totalElements - (this.pageSize * (this.pageIndex + 1));
          data.content.push(...new Array<EventDetailsShowsDto>(emptySize < 0 ? 0 : emptySize));
          this.dataSource.data.splice(this.pageIndex * this.pageSize,
            this.dataSource.data.length, ...data.content);
          this.dataSource.data = this.dataSource.data;

          this.checkQueryParams();
        } else {
          this.dataSource.data = null;
        }
      },
      error: err => {
        this.snackBarService.openErrorSnackbar(err);
      }
    });
  }

  clearDate() {
    this.showingForm.patchValue({
      date: null
    });
  }

  clearStartTime() {
    this.showingForm.patchValue({
      startTime: ''
    });
  }

  clearEndTime() {
    this.showingForm.patchValue({
      endTime: ''
    });
  }

  getPricingString(show: EventDetailsShowsDto): string {
    if(show.bookedOut){
      return '-';
    }
    if(show.lowestPrice === show.highestPrice){
      return '€' + show.lowestPrice;
    }
    return '€' + show.lowestPrice + ' - €' + show.highestPrice;
  }

  private updateFilter() {
    this.showingFilter.eventTitle = this.showingForm.controls['eventTitle'].value;
    this.showingFilter.locationName = this.showingForm.controls['locationName'].value;
    this.showingFilter.date = this.showingForm.controls['date'].value;
    this.showingFilter.startTime = this.showingForm.controls['startTime'].value;
    this.showingFilter.endTime = this.showingForm.controls['endTime'].value;
    this.showingFilter.minPrice = this.showingForm.controls['minPrice'].value;
    this.showingFilter.maxPrice = this.showingForm.controls['maxPrice'].value;
  }

  private checkQueryParams() {
    this.route.queryParams.subscribe(params => {
      if (
        !params.eventTitle
        && !params.locationName
        && !params.date
        && !params.startTime
        && !params.endTime
        && !params.minPrice
        && !params.maxPrice) {
        this.paramsEmpty = true;
      } else {
        this.paramsEmpty = false;
      }
    });
  }
}
