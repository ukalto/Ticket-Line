import {Injectable} from '@angular/core';
import {CanActivate, CanLoad} from '@angular/router';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {Location} from '@angular/common';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';

@Injectable({
  providedIn: 'root'
})
export class IsAdminGuard implements CanActivate, CanLoad {

  constructor(private authService: AuthenticationService, private location: Location,
              private snackBarService: SnackBarService) {
  }

  canActivate() {
    if (!this.authService.isAdmin()) {
      this.location.back();
      this.snackBarService.openWarningSnackBar('You must be an Administrator to access this page!');
      return false;
    }
    return true;
  }

  canLoad() {
    return undefined;
  }
}
