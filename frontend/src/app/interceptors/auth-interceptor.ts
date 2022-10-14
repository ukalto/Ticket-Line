import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { AuthenticationService } from '../services/authentication/authentication.service';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private readonly excludedRoutes = [
    this.globals.backendUri + '/authentication',
    this.globals.backendUri + '/customer/registration',
    this.globals.backendUri + '/event/',
  ];

  constructor(private authenticationService: AuthenticationService, private globals: Globals) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.shouldIntercept(request)) {
      const authenticationRequest = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + this.authenticationService.loadToken())
      });

      return next.handle(authenticationRequest);
    }

    return next.handle(request);
  }

  private shouldIntercept(request: HttpRequest<any>): boolean {
    if (!this.authenticationService.isLoggedIn()) {
      return false;
    }

    // Do not intercept authentication requests
    const isAuthenticationRequest = this.excludedRoutes.includes(request.url);
    if (isAuthenticationRequest) {
      return false;
    }

    const authorizationHeader = request.headers.get('Authorization');

    const isEmpty = (!authorizationHeader || authorizationHeader.trim() === '');
    if (isEmpty) {
      return true;
    }

    const parts = authorizationHeader.split(' ');
    const isBearer = parts.length === 2 && parts[0] === 'Bearer' && parts[1].trim() !== '';
    if (isBearer) {
      return false;
    }

    return true;
  }
}
