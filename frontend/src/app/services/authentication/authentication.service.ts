import {Injectable} from '@angular/core';
import {AuthenticateUserDto} from '../../dtos/authentication/authenticate-user.dto';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import jwt_decode from 'jwt-decode';
import {Globals} from '../../global/globals';
import {UserRole} from 'src/app/dtos/authentication/user-role.dto';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private readonly jwtTokenStorageKey = 'user.jwt';

  private authenticationBaseUri: string = this.globals.backendUri + '/authentication';

  constructor(private httpClient: HttpClient, private globals: Globals, private router: Router) {
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   *
   * @param authRequest User data
   */
  public login(authRequest: AuthenticateUserDto): Observable<string> {
    return this.httpClient.post(this.authenticationBaseUri, authRequest, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => this.persistToken(authResponse))
      );
  }


  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  public isLoggedIn() {
    return !!this.loadToken() && (this.getTokenExpirationDate(this.loadToken()).valueOf() > new Date().valueOf());
  }

  /**
   * Logs out the user (removes token) and navigates back to the login page
   */
  public logout() {
    sessionStorage.removeItem(this.jwtTokenStorageKey);
    this.router.navigate(['/login']);
  }

  loadToken() {
    return sessionStorage.getItem(this.jwtTokenStorageKey);
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole(): UserRole {
    const userIsUnauthenticated = this.loadToken() == null;
    if (userIsUnauthenticated) {
      return UserRole.roleUndefined;
    }

    const decoded: any = jwt_decode(this.loadToken());
    const roles: string[] = decoded.rol;

    if (roles.includes('ROLE_SUPER_ADMINISTRATOR')) {
      return UserRole.roleSuperAdmin;
    } else if (roles.includes('ROLE_ADMINISTRATOR')) {
      return UserRole.roleAdmin;
    } else {
      return UserRole.roleUser;
    }
  }

  userIdFromToken(jwt: string): number {
    const decoded: any = jwt_decode(jwt);

    return +decoded.uid;
  }

  getCurrentUserId(): number {
    const decodedJwt: any = jwt_decode(this.loadToken());

    return +decodedJwt.uid;
  }

  isAdmin(): boolean {
    const role = this.getUserRole();
    return role === UserRole.roleAdmin || role === UserRole.roleSuperAdmin;
  }

  getUserName(): string {
    const userIsUnauthenticated = this.loadToken() == null;
    if (userIsUnauthenticated) {
      return 'Account';
    }
    const decoded: any = jwt_decode(this.loadToken());
    return decoded.sub;
  }

  public persistToken(token: string) {
    sessionStorage.setItem(this.jwtTokenStorageKey, token);
  }

  private getTokenExpirationDate(token: string): Date {
    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);

    return date;
  }

}
