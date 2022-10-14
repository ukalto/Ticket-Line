import { Injectable } from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import { AuthenticationService } from '../services/authentication/authentication.service';
import {catchError, Observable} from 'rxjs';
import { Globals } from '../global/globals';

@Injectable()
export class ResponseInterceptor implements HttpInterceptor {
  constructor(private authenticationService: AuthenticationService, private globals: Globals) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authenticationUri = this.globals.backendUri + '/authentication';
    const registerUri = this.globals.backendUri + '/customer/registration';
    const notIntercepted = [authenticationUri, registerUri];

    // Do not intercept urls specified
    if (notIntercepted.includes(request.url)) {
      return next.handle(request);
    }

    return next.handle(request).pipe(
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse) {
          if (err.status === 401) {
            this.authenticationService.logout();
          }
        }
        throw new HttpErrorResponse(err);
      })
    );
  }
}
