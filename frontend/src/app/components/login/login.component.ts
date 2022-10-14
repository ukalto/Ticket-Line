import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { AuthenticateUserDto } from '../../dtos/authentication/authenticate-user.dto';
import { SnackBarService } from '../../services/snackbar/snack-bar.service';
import { AdministrationService } from 'src/app/services/administration/administration.service';


@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    loginForm: FormGroup;
    // After first submission attempt, form validation will start
    submitted = false;
    // Error flag
    error = false;
    hide = true;

    constructor(
        private formBuilder: FormBuilder,
        private authService: AuthenticationService,
        private router: Router,
        private snackBarService: SnackBarService,
        private administrationService: AdministrationService
    ) {
        this.loginForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(8)]]
        });

    }

    triggerPasswordReset() {
      this.administrationService
        .triggerPasswordResetByEmail(this.loginForm.get('email').value)
        .subscribe({
            next: () => this.snackBarService.openSuccessSnackbar('Sent password reset.'),
            error: (error) => this.snackBarService.openErrorSnackbar(error)
        });
    }

    ngOnInit() { }

    /**
     * Form validation will start after the method is called, additionally an AuthRequest will be sent
     */
    loginUser() {
        this.submitted = true;
        if (this.loginForm.valid) {
            const authRequest: AuthenticateUserDto = new AuthenticateUserDto(this.loginForm.controls.email.value,
                this.loginForm.controls.password.value);
            this.authenticateUser(authRequest);
        }
    }

    /**
     * Send authentication data to the authService. If the authentication was successfully,
     * the user will be forwarded to the news-entry page
     *
     * @param authRequest authentication data from the user login form
     */
    authenticateUser(authRequest: AuthenticateUserDto) {
        this.authService.login(authRequest).subscribe({
            next: () => {
                if (this.authService.isAdmin()) {
                    this.router.navigate(['/administration']);
                } else {
                    this.router.navigate(['/news']);
                }
            },
            error: error => {
                this.snackBarService.openErrorSnackbar(error);
            }
        });
    }
}
