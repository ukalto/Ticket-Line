import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { tap } from 'rxjs';
import { EventCategoryDto } from 'src/app/dtos/event/event-category.dto';
import { TopTenEventDto } from 'src/app/dtos/event/top-ten-event.dto';
import { EventService } from 'src/app/services/event/event.service';
import { SnackBarService } from 'src/app/services/snackbar/snack-bar.service';
import { ChartEntry } from '../top-ten-events-chart/top-ten-events-chart.component';
import { NgxMaterialTimepickerTheme } from 'ngx-material-timepicker';
import { EventSearchResultDto } from 'src/app/dtos/event/event-search-result.dto';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';


@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
  public readonly allCategories = 'all';

  public eventsForm: FormGroup;
  public topTenForm: FormGroup;
  public categories: EventCategoryDto[] = [];
  public chartData: ChartEntry[] = [];
  searchResults: EventSearchResultDto[] = [];
  imagePaths: SafeResourceUrl[] = [];
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
  pageNumber = 0;
  paginationSize = 10;
  lastLoadMoreRequestIndicatesNoMoreEntries = false;
  selectedTabIndex = 0;
  canReset = false;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly eventService: EventService,
    private readonly snackbarService: SnackBarService,
    private _sanitizer: DomSanitizer,
    private route: ActivatedRoute
  ) {
    this.route.queryParams.subscribe(params => {
      if (Object.keys(params).length !== 0) {
        this.selectedTabIndex = 1;
        this.canReset = true;
      }

      // fill search form with data from query params
      let duration: string = null;
      if (params.hours && params.hours) {
        duration = parseInt(params.hours, 10) + ':' + parseInt(params.hours, 10);
      }

      let category = null;
      if(params.categoryId){
        category = parseInt(params.categoryId, 10);
      }

      this.eventsForm = formBuilder.group({
        eventNameOrContent: [params.nameOrContent],
        duration: [duration],
        category: [category]
      });

      this.find(false, false);
    });

    this.topTenForm = this.formBuilder.group({
      category: [this.allCategories]
    });
  }

  ngOnInit(): void {
    this.eventService.getEventCategories()
      .pipe(
        tap(() => this.loadChartData())
      )
      .subscribe({
        next: (categories) => {
          this.categories = categories;
          this.categories.sort((a, b) => a.id - b.id);
        },
        error: (error) => this.snackbarService.openErrorSnackbar(error)
      });

  }

  public selectionChanged() {
    this.loadChartData();
  }

  find(addToOldValues = false, updateQueryParams = true) {
    if (!addToOldValues) {
      this.pageNumber = 0;
    }

    let queryParams = this.getQueryParams();
    if(updateQueryParams){
      this.router.navigate([], { queryParams });
    }
    queryParams = this.getQueryParams(true);

    this.eventService.getEventsFiltered(queryParams).subscribe({
      next: data => {
        this.lastLoadMoreRequestIndicatesNoMoreEntries = data.length < this.paginationSize;

        if (addToOldValues) {
          this.searchResults = this.searchResults.concat(data);
        } else {
          this.searchResults = data;
        }

        // handle images
        if (!addToOldValues) {
          this.imagePaths = [];
        }
        data.forEach(event => {
          if (event.imageFile) {
            this.imagePaths.push(
              this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + event.imageFile)
            );
          }
        });
      },
      error: error => {
        this.snackbarService.openErrorSnackbar(error);
      }
    });
    this.pageNumber++;
  }

  getQueryParams(includePagination = false): any {
    const eventNameOrContent = this.eventsForm.value.eventNameOrContent;
    const categoryId = this.eventsForm.value.category;

    const formDuration = this.eventsForm.value.duration;
    let durationHours: number = null;
    let durationMinutes: number = null;
    if (formDuration != null) {
      const dateDuration = new Date(0, 0, 0, formDuration.split(':')[0], formDuration.split(':')[1]);
      durationHours = dateDuration.getHours();
      durationMinutes = dateDuration.getMinutes();
    }

    const queryParams: any = {};
    if (eventNameOrContent) {
      queryParams.nameOrContent = eventNameOrContent;
    }
    if (categoryId) {
      queryParams.categoryId = categoryId;
    }
    if (durationHours !== null) {
      queryParams.hours = durationHours;
    }
    if (durationMinutes !== null) {
      queryParams.minutes = durationMinutes;
    }
    if(includePagination){
      queryParams.pageSize = this.paginationSize;
      queryParams.pageNumber = this.pageNumber;
    }
    return queryParams;
  }

  loadMore() {
    this.find(true, false);
  }

  resetFilter() {
    this.eventsForm.reset();
    this.find();
  }

  categoryIdToName(id: number): string {
    return this.categories[id - 1].displayName;
  }

  private loadChartData() {
    const categoryValue = this.topTenForm.controls.category.value === this.allCategories
      ? null : this.topTenForm.controls.category.value;

    this.eventService.getTopTenEventsByCategory(categoryValue)
      .subscribe({
        next: (topTenEvents) => {
          this.chartData = topTenEvents.map(e => this.eventToChartComponent(e));
        },
        error: (error) => console.error(error)
      });
  }

  private eventToChartComponent(event: TopTenEventDto): ChartEntry {
    return {
      name: event.title,
      value: event.totalBookings,
      extra: {
        id: event.id
      }
    };
  }
}
