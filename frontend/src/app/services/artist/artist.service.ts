import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../../global/globals';
import {ArtistDto} from '../../dtos/artist/artist.dto';
import {Observable} from 'rxjs';
import {ArtistEventDto} from '../../dtos/artist/artist-event.dto';
import {PageDto} from '../../dtos/page/page.dto';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {

  private artistBaseUri: string = this.globals.backendUri + '/artist';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Creates an artist
   *
   * @param artistRequest is an artistDTO that should be created
   * @returns created artist with responseArtistDTO
   */
  createArtist(artistRequest: ArtistDto): Observable<ArtistDto> {
    return this.httpClient.post<ArtistDto>(this.artistBaseUri, artistRequest);
  }

  /**
   * Returns list of artists that contain a given string
   *
   * @param name substring of artists to be searched for
   * @returns list of artists that contain the substring
   */
  getArtistsByNameFilter(name: string): Observable<ArtistDto[]> {
    let queryParams = new HttpParams();
    queryParams = queryParams.set('nameFilter', name);
    return this.httpClient.get<ArtistDto[]>(this.artistBaseUri + 's', {params: queryParams});
  }

  filterArtistEvents(queryParams): Observable<PageDto<ArtistEventDto>> {
    return this.httpClient.get<PageDto<ArtistEventDto>>(this.globals.backendUri + '/artists-events',
      {params: queryParams});
  }

}
