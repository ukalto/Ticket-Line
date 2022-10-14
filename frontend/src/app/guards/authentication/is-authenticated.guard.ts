import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';

@Injectable({
  providedIn: 'root'
})
export class IsAuthenticatedGuard implements CanActivate {
  constructor(private authService: AuthenticationService, private router: Router,
              private snackBarService: SnackBarService) {
  }

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      this.snackBarService.openWarningSnackBar('You must be logged in to access this page');
      return false;
    }
    return true;
  }
}
