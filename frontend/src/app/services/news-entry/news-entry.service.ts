import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {NewsEntry} from '../../dtos/news-entry/news-entry.dto';
import {Observable} from 'rxjs';
import {Globals} from '../../global/globals';
import {NewsEntryDetailed} from 'src/app/dtos/news-entry/news-entry-detailed.dto';
import {NewsEntryOverview} from 'src/app/dtos/news-entry/news-entry-overview.dto';
import {AuthenticationService} from '../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class NewsEntryService {

  private newsBaseUri: string = this.globals.backendUri + '/news';
  private newsEntryBaseUri: string = this.newsBaseUri + '-entry';

  constructor(private httpClient: HttpClient, private globals: Globals, private authenticationService: AuthenticationService) {
  }

  /**
   * Loads all newsEntrys from the backend
   */
  getNews(): Observable<NewsEntryOverview[]> {
    let uri = this.newsBaseUri;
    if (this.authenticationService.isLoggedIn()) {
      uri = uri + '/' + this.authenticationService.getCurrentUserId();
    }
    return this.httpClient.get<NewsEntryOverview[]>(uri);
  }

  getReadNews(): Observable<NewsEntryOverview[]> {
    let uri = this.newsBaseUri;
    if (this.authenticationService.isLoggedIn()) {
      uri = uri + '/' + this.authenticationService.getCurrentUserId() + '/archive';
    }
    return this.httpClient.get<NewsEntryOverview[]>(uri);
  }

  /**
   * Loads specific newsEntry from the backend
   *
   * @param id of newsEntry to load
   */
  getNewsEntryById(id: number): Observable<NewsEntryDetailed> {
    return this.httpClient.get<NewsEntryDetailed>(this.newsEntryBaseUri + '/' + id);
  }

  /**
   * Persists newsEntry to the backend
   *
   * @param newsEntry to persist
   */
  createNewsEntry(newsEntry: NewsEntry): Observable<NewsEntry> {
    return this.httpClient.post<NewsEntry>(this.newsEntryBaseUri, newsEntry);
  }

  /**
   * Stores a single file
   *
   * @param id
   * @param image
   */
  saveImage(id: number, image: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', image, image.name);
    return this.httpClient.post<any>(this.newsEntryBaseUri + '/' + id + '/image', formData);
  }

  registerNewsRead(newsId: number): Observable<any> {
    return this.httpClient.post<any>
    (this.newsEntryBaseUri + '/' + newsId + '/viewing/' + this.authenticationService.getCurrentUserId(), null);
  }
}
