import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../../global/globals';
import {LocationDto} from '../../dtos/location/location.dto';
import {Observable} from 'rxjs';
import {LocationResponseDto} from '../../dtos/location/location-response.dto';
import {SeatingPlan} from '../../dtos/seating-plan/seating-plan-creation.dto';
import {SeatingPlanResponse} from 'src/app/dtos/seating-plan/seating-plan-response.dto';
import {LocationEventDto} from '../../dtos/location/location-event.dto';
import {PageDto} from '../../dtos/page/page.dto';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/location';
  private seatingPlanUriExtension = '/seating-plan';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Create Location.
   *
   * @param locationRequest Location data
   */
  createLocation(locationRequest: LocationDto): Observable<LocationDto> {
    return this.httpClient.post<LocationDto>(this.locationBaseUri, locationRequest);
  }

  /**
   * Get all locations, ordered by name descending
   */
  getLocations(): Observable<LocationDto[]> {
    return this.httpClient.get<LocationDto[]>(this.locationBaseUri + 's');
  }

  /**
   * Search locations
   *
   * @param searchCriteria used for filtering matching locations
   * @param limit limits the amount of results returned
   */
  searchLocations(searchCriteria: LocationDto, limit: number): Observable<LocationResponseDto[]> {

    let queryParams = new HttpParams();
    if (searchCriteria.name !== null) {
      queryParams = queryParams.set('name', searchCriteria.name);
    }
    if (searchCriteria.country !== null) {
      queryParams = queryParams.set('country', searchCriteria.country);
    }
    if (searchCriteria.town !== null) {
      queryParams = queryParams.set('town', searchCriteria.town);
    }
    if (searchCriteria.town !== null) {
      queryParams = queryParams.set('street', searchCriteria.street);
    }
    if (searchCriteria.postalCode !== null) {
      queryParams = queryParams.set('postalCode', searchCriteria.postalCode);
    }

    queryParams = queryParams.set('limit', limit);

    return this.httpClient.get<LocationResponseDto[]>(this.locationBaseUri + 's', {params: queryParams});
  }

  /**
   * Create seating plan.
   *
   * @param data seating plan data
   */
  createSeatingPlan(data: SeatingPlan): Observable<SeatingPlan> {
    const uri = this.locationBaseUri + '/' + data.locatedIn + this.seatingPlanUriExtension;
    return this.httpClient.post<SeatingPlan>(uri, data);
  }

  /**
   * fetches all seating plans by location id
   *
   * @param id id of the location to be fetched
   * @returns array of seating plans
   */
  getSeatingPlanNamesByLocation(id: number): Observable<SeatingPlanResponse[]> {
    return this.httpClient.get<SeatingPlanResponse[]>(this.locationBaseUri + '/' + id + this.seatingPlanUriExtension + 's');
  }

  /**
   * fetches one seating plan by id
   *
   * @param id id of the seating plan to be fetched
   * @returns seating plan object
   */
  getSeatingPlanById(id: number): Observable<SeatingPlanResponse> {
    return this.httpClient.get<SeatingPlanResponse>(this.locationBaseUri + this.seatingPlanUriExtension + '/' + id);
  }

  /**
   * fetches the filtered locations with the according events
   *
   * @param queryParams holds the filter information
   */
  filterLocationEvents(queryParams): Observable<PageDto<LocationEventDto>> {
    return this.httpClient.get<PageDto<LocationEventDto>>(this.globals.backendUri + `/locations-events`,
      {params: queryParams});
  }
}
