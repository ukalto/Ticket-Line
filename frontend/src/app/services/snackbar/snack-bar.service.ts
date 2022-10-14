import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AuthenticationService} from '../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class SnackBarService {

  constructor(private _snackBar: MatSnackBar, private authenticationService: AuthenticationService) {
  }

  /**
   * Opens up a success snackBar (alert) with the message.
   *
   * @param message message to be displayed in alert
   */
  openSuccessSnackbar(message: string) {
    this._snackBar.open(message, 'Close', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['green-snackbar', 'login-snackbar']
    });
  }

  /**
   * Opens up a warning snackBar (alert) with the message.
   *
   * @param message message to be displayed in alert
   */
  openWarningSnackBar(message: string) {
    this._snackBar.open(message, 'Close', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['yellow-snackbar', 'warning-snackbar']
    });
  }

  /**
   * Opens up an error snackBar (alert) with the message.
   *
   * @param message message to be displayed in alert
   */
  openErrorSnackbar(message: string | object);
  openErrorSnackbar(message: any) {

    if (message.status === 0) {
      message = 'Server did not respond!';
    } else if (typeof message.error === 'object') {
      message = message.error.error;
    } else if (message.error) {
      message = message.error;
    }

    this._snackBar.open(message, 'Close', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['red-snackbar', 'login-snackbar']
    });
  }
}
