import {Component, Input} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../../services/authentication/authentication.service';
import {PasswordConfirmationValidator} from '../../validators/password-confirmation.validator';
import {EditUserDto} from '../../../dtos/user/edit-user.dto';
import {EditUserAccessDto} from '../../../dtos/user/edit-user-access.dto';
import {SnackBarService} from '../../../services/snackbar/snack-bar.service';
import {UserService} from '../../../services/user/user.service';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {WarningDialogComponent} from '../../warning-dialog/warning-dialog.component';

@Component({
  selector: 'app-update-password',
  templateUrl: './update-password-email.component.html',
  styleUrls: ['./update-password-email.component.scss']
})
export class UpdatePasswordEmailComponent {

  @Input() editUserDto: EditUserDto;
  updateAccessForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  hideCurrentPassword = true;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(public authenticationService: AuthenticationService,
              private formBuilder: FormBuilder,
              private passwordConfirmationValidator: PasswordConfirmationValidator,
              private snackBarService: SnackBarService,
              private userService: UserService,
              private location: Location,
              private dialog: MatDialog) {

    this.updateAccessForm = this.formBuilder.group({
        email: [this.authenticationService.getUserName(), [Validators.email, Validators.required]],
        currentPassword: ['', [Validators.minLength(8)]],
        password: ['', [Validators.minLength(8)]],
        confirmPassword: ['']
      }, {
        validators: [this.passwordConfirmationValidator
          .matchPasswordsNotRequired('password', 'confirmPassword'),
          this.passwordConfirmationValidator
            .allFilledOutOrNot('currentPassword', 'password', 'confirmPassword')]
      }
    );
  }

  back() {
    this.location.back();
  }

  deleteAccount(): void {
    const dialogRef = this.dialog.open(WarningDialogComponent, {
      data: {
        title: `Delete User: '${this.updateAccessForm.controls.email.value}'`,
        text: 'Do you really want to delete your Account permanently?',
        note: 'Bought tickets & invoices can no longer be printed.\n Reservations will be deleted permanently!',
        submitButton: `Delete permanently`
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.deleteUser(this.editUserDto.id).subscribe({
            next: data => {
              this.snackBarService.openSuccessSnackbar('You have successfully deleted your account!');
              this.authenticationService.logout();
            },
            error: error => {
              this.snackBarService.openErrorSnackbar(error);
            }
          }
        );
      }
    });
  }

  updateAccess(): void {
    this.submitted = true;

    if (this.updateAccessForm.valid) {
      this.userService.updateUserAccess(this.editUserDto.id,
        new EditUserAccessDto(this.updateAccessForm.controls.email.value,
          this.updateAccessForm.controls.currentPassword.value,
          this.updateAccessForm.controls.password.value)).subscribe({
        next: data => {
          this.authenticationService.persistToken(data);
          this.updateAccessForm.reset({email: this.authenticationService.getUserName()});
          this.snackBarService.openSuccessSnackbar('Successfully changed access information!');
        },
        error: error => {
          this.snackBarService.openErrorSnackbar(error);
        }
      });
    }
  }

}
