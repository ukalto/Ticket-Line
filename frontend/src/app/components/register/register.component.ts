import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticateUserDto } from '../../dtos/authentication/authenticate-user.dto';
import { CustomerService } from '../../services/customer/customer.service';
import { RegisterCustomerDto } from 'src/app/dtos/customer/register-customer.dto';
import { SnackBarService } from '../../services/snackbar/snack-bar.service';
import { PasswordConfirmationValidator } from '../validators/password-confirmation.validator';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(private formBuilder: FormBuilder,
    private readonly customerService: CustomerService,
    private readonly router: Router,
    private readonly snackBarService: SnackBarService,
    private readonly passwordConfirmationValidator: PasswordConfirmationValidator) {
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: [this.passwordConfirmationValidator
        .matchPasswordsRequired('password', 'confirmPassword')] }
    );
  }

  ngOnInit(): void { }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  createUser() {
    this.submitted = true;
    if (!this.registerForm.valid) {
      return;
    }

    const registrationRequest: AuthenticateUserDto = new AuthenticateUserDto(
      this.registerForm.controls.email.value,
      this.registerForm.controls.password.value
    );

    this.registerUser(registrationRequest);
  }

  /**
   * Send registration data to the authService.
   * If the registration was successfully, the user will be forwarded to the login page.
   *
   * @param registrationRequest data from the user register form.
   */
  registerUser(registrationRequest: RegisterCustomerDto) {
    this.customerService.createCustomer(registrationRequest)
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
          this.snackBarService.openSuccessSnackbar('Customer Account was successfully created!');
        },
        error: error => {
          this.snackBarService.openErrorSnackbar(error);
        }
      });
  }

}
