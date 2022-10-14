import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {tap} from 'rxjs/operators';
import {debounceTime, distinctUntilChanged, fromEvent, Observable} from 'rxjs';
import {EventService} from '../../services/event/event.service';
import {EventDto} from '../../dtos/event/event.dto';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements AfterViewInit {

  @ViewChild('input', { static: true }) input: ElementRef;
  filteredEvents: Observable<EventDto[]>;

  constructor(public authService: AuthenticationService,
              private eventService: EventService,
              private router: Router) {
  }

  ngAfterViewInit(): void {
    fromEvent(this.input.nativeElement,
      'keyup').pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap((event: KeyboardEvent) => {
        this.filteredEvents = this.eventService.filterEvents(this.input.nativeElement.value);
      })
    ).subscribe();
    this.filteredEvents = this.eventService.filterEvents(this.input.nativeElement.value);
  }

  eventDetails(id: number) {
    this.router.navigate([`/events/${id}`]);
    this.input.nativeElement.value = '';
    this.filteredEvents = this.eventService.filterEvents(this.input.nativeElement.value);
  }

}
