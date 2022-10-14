import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventCategoryDto } from 'src/app/dtos/event/event-category.dto';
import { EventDto } from 'src/app/dtos/event/event.dto';
import { PriceAndSectorName } from 'src/app/dtos/event/price-and-sectorname.dto';
import { Showing } from 'src/app/dtos/event/showing-normal.dto';
import { NonSeatSpecifier, SeatSpecifier } from 'src/app/dtos/seating-plan/seating-plan-visual.dto';
import { Globals } from '../../global/globals';
import { TopTenEventDto } from 'src/app/dtos/event/top-ten-event.dto';
import { EventSearchResultDto } from 'src/app/dtos/event/event-search-result.dto';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventBaseUri: string = this.globals.backendUri + '/event';
  private showingBaseUri: string = this.globals.backendUri + '/showing/';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * fetches all events
   *
   * @returns array of all events
   */
  getEvents(): Observable<EventDto[]> {
    return this.httpClient.get<EventDto[]>(this.eventBaseUri + 's');
  }

  /**
   * Filters events matching with title input
   *
   * @param title title input
   */
  filterEvents(title: string): Observable<EventDto[]> {
    return this.httpClient.get<EventDto[]>(this.globals.backendUri + `/events-search`,
      {params: {title}});
  }

  /**
   * fetches all event categories.
   *
   * @returns array of event categories.
   */
  getEventCategories(): Observable<EventCategoryDto[]> {
    return this.httpClient.get<EventCategoryDto[]>(this.eventBaseUri + '/categories');
  }

  /**
   * creates an event.
   *
   * @param event event to be created.
   * @returns created event with id.
   */
  createEvent(event: EventDto): Observable<EventDto> {
    return this.httpClient.post<EventDto>(this.eventBaseUri, event);
  }

  /**
   * Returns the showing fitting to given the id
   *
   * @param showingId the id of the showing
   * @returns showing with the given id
   */
  getShowing(showingId): Observable<Showing> {
    const uri = this.showingBaseUri + showingId;

    return this.httpClient.get<Showing>(uri);
  }

  /**
   * Returns the showing fitting to given the id and event id
   *
   * @param showingId the id of the showing
   * @param eventId the event id of the showing
   * @returns showing with the given id and event id
   */
  getShowingByIdAndEventId(showingId, eventId): Observable<Showing> {
    const uri = this.eventBaseUri + '/' + eventId + '/showing/' + showingId;

    return this.httpClient.get<Showing>(uri);
  }

  /**
   * Returns prices and sector names of all sectors fitting to the showing
   *
   * @param showingId id of the showing
   * @returns prices and sector names
   */
  getShowingPrices(showingId: number): Observable<PriceAndSectorName[]> {
    const uri = this.showingBaseUri + showingId + '/prices';

    return this.httpClient.get<PriceAndSectorName[]>(uri);
  }

  /**
   * Returns all alredy booked seats fitting to the showing
   *
   * @param showingId id of the showing
   * @returns list of seats
   */
  getBookedSeats(showingId: number): Observable<SeatSpecifier[]> {
    const uri = this.showingBaseUri + showingId + '/booked-seats';

    return this.httpClient.get<SeatSpecifier[]>(uri);
  }

  /**
   * Returns all alredy booked non-seats fitting to the showing
   *
   * @param showingId id of the showing
   * @returns list of non-seats
   */
  getBookedNonSeats(showingId: number): Observable<NonSeatSpecifier[]> {
    const uri = this.showingBaseUri + showingId + '/booked-non-seats';

    return this.httpClient.get<NonSeatSpecifier[]>(uri);
  }

  /**
   * Retrieves the top ten events of a given category.
   * Note: May return less than 10 results.
   *
   * @param categoryId to filter for.
   * @returns the top ten events of a given category.
   */
  public getTopTenEventsByCategory(categoryId?: number): Observable<TopTenEventDto[]> {
    const query = categoryId ? `?categoryId=${categoryId}` : '';

    return this.httpClient.get<TopTenEventDto[]>(`${this.globals.backendUri}/events/top-ten` + query);
  }

  /**
   * Stores a single file
   *
   * @param id
   * @param image
   */
  public saveImage(id: number, image: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', image, image.name);
    return this.httpClient.post<any>(this.eventBaseUri + '/' + id + '/image', formData);
  }

  /**
   * Returns true if the seating plan with the given id is occupied in the given time frame.
   *
   * @param seatingPlanId seating plan id
   * @param start starting time
   * @param end end time
   * @returns true if occupied, false if not.
   */
  public isOccupied(seatingPlanId: number, start: Date, end: Date): Observable<boolean> {
    let queryParams = new HttpParams();
    if (start !== null) {
      const tzoffset = start.getTimezoneOffset() * 60000; //offset in milliseconds
      const localISOTime = (new Date(start.getTime() - tzoffset)).toISOString().slice(0, -1);
      queryParams = queryParams.set('start', localISOTime);
    }
    if (end !== null) {
      const tzoffset = end.getTimezoneOffset() * 60000; //offset in milliseconds
      const localISOTime = (new Date(end.getTime() - tzoffset)).toISOString().slice(0, -1);
      queryParams = queryParams.set('end', localISOTime);
    }
    return this.httpClient.get<boolean>(`${this.showingBaseUri}${seatingPlanId}/occupation`, { params: queryParams });
  }

  /** Returns events, that fit the search criteria
   *
   * @param nameOrContent the name or the description of the event
   * @param categoryId the id of the event-category
   * @param durationHours how long the event is in hours
   * @param durationMinutes are added to the hours
   * @returns a list of events
   */
  public getEventsFiltered(queryParams: any): Observable<EventSearchResultDto[]> {
    return this.httpClient.get<EventSearchResultDto[]>(this.globals.backendUri + '/events/filter', { params: queryParams });
  }
}
