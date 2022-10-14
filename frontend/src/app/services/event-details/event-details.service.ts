import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../global/globals';
import {Observable} from 'rxjs';
import {EventDetailsDto} from '../../dtos/event/event-details.dto';
import {EventCategoryDto} from '../../dtos/event/event-category.dto';
import {EventDetailsShowsDto} from '../../dtos/event/event-details-shows.dto';
import {PageDto} from '../../dtos/page/page.dto';
import {EventShowingFilterDto} from '../../dtos/event/event-showing-filter.dto';
import { formatDate } from '@angular/common';
import {Router} from '@angular/router';



@Injectable({
  providedIn: 'root'
})
export class EventDetailsService {

  private eventBaseUri: string = this.globals.backendUri + '/event';

  constructor(private httpClient: HttpClient,
              private globals: Globals,
              private router: Router) {
  }

  /**
   * Get Event by id.
   *
   * @return returns event
   */
  findById(id: number): Observable<EventDetailsDto> {
    return this.httpClient.get<EventDetailsDto>(`${this.eventBaseUri}/${id}`);
  }

  /**
   * Get EventCategory by id.
   *
   * @return returns EventCategory
   */
  findEventCategoryById(id: number): Observable<EventCategoryDto> {
    return this.httpClient.get<EventCategoryDto>(`${this.eventBaseUri}/${id}/category`);
  }

  /**
   * Get Shows by id.
   *
   * @return returns list of shows with given id
   */
  findShowingsById(id: number, pageIndex: number, pageSize: number): Observable<PageDto<EventDetailsShowsDto>> {
    return this.httpClient.get<PageDto<EventDetailsShowsDto>>(`${this.eventBaseUri}/${id}/showings/?page=${pageIndex}&size=${pageSize}`);
  }

  /**
   * Filters showing based on showing filter
   *
   * @param showingFilter consist of values to filter showing
   * @param pageIndex index of returned page
   * @param pageSize size of returned page
   */
  filterShowings(showingFilter: EventShowingFilterDto, pageIndex: number, pageSize: number): Observable<PageDto<EventDetailsShowsDto>> {
    const queryParams: any = {};
    if(showingFilter.eventTitle){
      queryParams.eventTitle = showingFilter.eventTitle;
    }
    if(showingFilter.locationName){
      queryParams.locationName = showingFilter.locationName;
    }
    if(showingFilter.date){
      queryParams.date = formatDate(showingFilter.date, 'yyyy-MM-dd', 'en_US');
    }
    if(showingFilter.startTime){
      queryParams.startTime = showingFilter.startTime;
    }
    if(showingFilter.endTime){
      queryParams.endTime = showingFilter.endTime;
    }

    if(showingFilter.minPrice || +showingFilter.minPrice === 0){
      queryParams.minPrice = showingFilter.minPrice;
    }
    if(showingFilter.maxPrice || +showingFilter.maxPrice === 0){
      queryParams.maxPrice = showingFilter.maxPrice;
    }

    queryParams.page = pageIndex;
    queryParams.size = pageSize;

    this.router.navigate([], {queryParams});
    return this.httpClient.get<PageDto<EventDetailsShowsDto>>(this.globals.backendUri + '/showings', { params: queryParams });
  }
}
