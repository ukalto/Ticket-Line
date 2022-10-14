import { Injectable } from '@angular/core';
import { CanActivate} from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { SnackBarService } from '../../services/snackbar/snack-bar.service';
import { Location } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class IsNotAdminGuard implements CanActivate {
  constructor(private authService: AuthenticationService, private location: Location,
    private snackBarService: SnackBarService) { }

  canActivate(): boolean {
    if (this.authService.isAdmin()) {
      this.location.back();
      this.snackBarService.openWarningSnackBar('Not allowed to load this page:'
        + ' You can\'t access this page as an Administrator!'
        + ' Please login as a regular user to view this page.');
      return false;
    }
    return true;
  }

}
