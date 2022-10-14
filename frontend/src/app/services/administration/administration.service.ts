import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../global/globals';
import {UserRole} from '../../dtos/authentication/user-role.dto';
import {Observable} from 'rxjs';
import {UserDto} from '../../dtos/user/user.dto';
import {PageDto} from '../../dtos/page/page.dto';

@Injectable({
  providedIn: 'root'
})
export class AdministrationService {

  private administrationBaseUri: string = this.globals.backendUri;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Find users by email
   *
   * @param email email to be matched with
   * @param role role of the searching user
   * @param pageIndex index page requested
   * @param pageSize page size requested
   */
  findUsersByEmail(email: string, role: UserRole, pageIndex: number, pageSize: number): Observable<PageDto<UserDto>> {
    const dest = role === UserRole.roleSuperAdmin ? 'super-admin' : 'admin';
    return this.httpClient.get<PageDto<UserDto>>(
      `${this.administrationBaseUri}/users/${dest}?page=${pageIndex}&size=${pageSize}&email=${email}`);
  }

  /**
   * Updates the isBlocked status of a user.
   *
   * @param id of the user to update.
   * @param role of the user.
   * @param isBlocked defines whether the user should be blocked or unblocked.
   * @returns an Observable<UserDto> resolving to the updated user.
   */
  updateUserIsBlocked(id: number, role: UserRole, isBlocked: boolean): Observable<UserDto> {
    const dest = role === UserRole.roleSuperAdmin ? 'admin' : 'customer';
    return this.httpClient.patch<UserDto>(`${this.administrationBaseUri}/user/${dest}/${id}`, isBlocked);
  }

  /**
   * Triggers a password reset using the ID as an identifying property.
   *
   * @param id of the user to trigger the password reset for.
   * @returns an empty Observable.
   */
  public triggerPasswordReset(id: number): Observable<void> {
    return this.httpClient.post<void>(`${this.globals.backendUri}/user/${id}/password-reset`, null);
  }

  /**
   * Triggers a password reset using the e-mail as an identifying property.
   *
   * @param email of the user to trigger the password reset for.
   * @returns an empty Observable.
   */
  public triggerPasswordResetByEmail(email: string): Observable<void> {
    return this.httpClient.post<void>(`${this.globals.backendUri}/users/password-reset`, {
      email
    });
  }

  public setNewPassword(id: number, newPassword: string, resetJwt: string): Observable<void> {
    const headers = {};
    headers['Authorization'] = resetJwt;

    return this.httpClient.patch<void>(`${this.globals.backendUri}/user/${id}`, {
      newPassword
    }, {
      headers: {...headers}
    });
  }
}
