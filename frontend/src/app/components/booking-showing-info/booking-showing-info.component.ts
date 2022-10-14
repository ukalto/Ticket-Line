import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { BookingService } from 'src/app/services/booking/booking.service';
import { EventDetailsService } from 'src/app/services/event-details/event-details.service';
import { SnackBarService } from 'src/app/services/snackbar/snack-bar.service';

@Component({
  selector: 'app-booking-showing-info',
  templateUrl: './booking-showing-info.component.html',
  styleUrls: ['./booking-showing-info.component.scss']
})
export class BookingShowingInfoComponent implements OnInit {
  @Input() eventId: number;
  @Input() showingId: number;

  eventName = 'Event name';
  locationName = 'Location name';
  showingDate = '22.01.2022, 15:00-16:00';
  imageReference = null;

  constructor(
    private eventDetailsService: EventDetailsService,
    private snackBarService: SnackBarService,
    private bookingService: BookingService,
    private _sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.eventDetailsService.findById(this.eventId).subscribe({
      next: data => {
        this.eventName = data.title;
        this.imageReference = data.file;
        this.imageReference = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + data.file);
      }
    });

    this.bookingService.getShowingDetails(this.showingId).subscribe({
      next: data => {
        if (!data) {
          return;
        }
        this.locationName = data.location + ' - ' + data.room;
        this.showingDate = data.date + ' ' + data.time;
      }
    });
  }

}
