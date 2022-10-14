import {Component} from '@angular/core';
import {EditUserDto} from '../../dtos/user/edit-user.dto';
import {UserService} from '../../services/user/user.service';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';

@Component({
  selector: 'app-update-account',
  templateUrl: './update-account.component.html',
  styleUrls: ['./update-account.component.scss']
})
export class UpdateAccountComponent {

  editUserDto: EditUserDto;
  // After first submission attempt, form validation will start
  groupInput = true;
  isDataLoaded = false;

  constructor(private userService: UserService,
              private authenticationService: AuthenticationService,
              private snackBarService: SnackBarService) {

    this.userService.findUserByEmail(this.authenticationService.getUserName()).subscribe({
      next: data => {
        this.editUserDto = data;
        if (this.editUserDto && this.editUserDto.cardOwner && this.editUserDto.cardNumber
          && this.editUserDto.cardExpirationDate && this.editUserDto.cardCvv) {
          this.groupInput = false;
        }
        this.isDataLoaded = true;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

}
