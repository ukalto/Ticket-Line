import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AdministrationService } from 'src/app/services/administration/administration.service';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { SnackBarService } from 'src/app/services/snackbar/snack-bar.service';
import { PasswordConfirmationValidator } from '../validators/password-confirmation.validator';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  userId: number;
  jwt: string;
  passwordResetForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(
    private readonly administrationService: AdministrationService,
    private readonly formBuilder: FormBuilder,
    private readonly passwordConfirmationValidator: PasswordConfirmationValidator,
    private readonly snackbarService: SnackBarService,
    private readonly activedRoute: ActivatedRoute,
    private readonly authenticationService: AuthenticationService,
  ) {
    this.passwordResetForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(8)]],
    }, { validators: [this.passwordConfirmationValidator.matchPasswordsRequired('password', 'confirmPassword')] }
    );
  }

  ngOnInit(): void {
    this.activedRoute.queryParams.subscribe({
      next: (params) => {
        const { jwt } = params;

        this.jwt = jwt;
        this.userId = this.authenticationService.userIdFromToken(this.jwt);
      },
      error: error => this.snackbarService.openErrorSnackbar(error)
    });
  }

  resetPassword() {
    const newPassword = this.passwordResetForm.get('password').value;

    this.administrationService.setNewPassword(this.userId, newPassword, this.jwt)
      .subscribe({
        next: () => this.snackbarService.openSuccessSnackbar('Succesfully reset password'),
        error: error => this.snackbarService.openErrorSnackbar(error)
      });
  }

}
