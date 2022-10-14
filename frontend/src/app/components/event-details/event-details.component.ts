import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {Location} from '@angular/common';
import {EventDetailsDto} from '../../dtos/event/event-details.dto';
import {EventDetailsService} from '../../services/event-details/event-details.service';
import {Router} from '@angular/router';
import {ActivatedRoute} from '@angular/router';
import {EventCategoryDto} from '../../dtos/event/event-category.dto';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {EventDetailsShowsDto} from '../../dtos/event/event-details-shows.dto';
import {AuthenticationService} from '../../services/authentication/authentication.service';


@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements AfterViewInit {

  @ViewChild('input', {static: true}) input: ElementRef;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  dataSource: MatTableDataSource<EventDetailsShowsDto>;
  pageIndex = 0;
  pageSize = 10;
  pageSizeOptions = [5, 10, 25, 100];
  displayedColumns: string[] = ['date', 'time', 'location', 'room', 'lowestPrice', 'bookNow'];
  event: EventDetailsDto;
  eventCategory: EventCategoryDto;
  shows: EventDetailsShowsDto;
  imagePath: SafeResourceUrl;
  currentRoute: string;
  eventId: number;


  constructor(private location: Location,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private eventDetailsService: EventDetailsService,
              public authenticationService: AuthenticationService,
              private snackBarService: SnackBarService,
              private _sanitizer: DomSanitizer) {
    this.imagePath = null;
    this.event = null;
    this.dataSource = new MatTableDataSource<EventDetailsShowsDto>([]);
    this.currentRoute = '';
  }

  refreshTable() {
    this.getShows();
  }

  back(): void {
    this.router.navigate(['events']).then();
  }

  findEventCategory(): void {
    this.eventDetailsService.findEventCategoryById(this.event.categoryId).subscribe({
      next: (dataCategory) => {
        this.eventCategory = dataCategory;
      },
      error: err => {
        console.error('Error fetching event-details', err.message);
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.activatedRoute.paramMap.subscribe(params => {
      this.eventId = +params.get('id');
      this.eventDetailsService.findById(this.eventId).subscribe({
        next: (data) => {
          if (data != null) {
            this.event = data;
            if (data.file !== '') {
              this.imagePath = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + data.file);
            }
            this.findEventCategory();
            this.getShows();
          } else {
            this.router.navigate(['events']).then();
            this.snackBarService.openErrorSnackbar('No existing event with this id!');
          }
        },
        error: err => {
          console.error('Error fetching event-details', err.message);
        }
      });
    });
  }

  changePaginator(event) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.refreshTable();
  }

  private getShows(): void {
    this.eventDetailsService.findShowingsById(this.event.id, this.pageIndex, this.pageSize).subscribe({
      next: (data) => {
        if (data != null) {
          const emptySize = data.totalElements - (this.pageSize * (this.pageIndex + 1));
          data.content.push(...new Array<EventDetailsShowsDto>(emptySize < 0 ? 0 : emptySize));
          this.dataSource.data.splice(this.pageIndex * this.pageSize,
            this.dataSource.data.length, ...data.content);
          this.dataSource.data = this.dataSource.data;
          this.dataSource.paginator = this.paginator;
        } else {
          this.dataSource.data = null;
        }
      },
      error: err => {
        this.snackBarService.openErrorSnackbar(err);
      }
    });
  }
}
