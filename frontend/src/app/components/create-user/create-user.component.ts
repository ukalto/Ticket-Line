import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserRole} from 'src/app/dtos/authentication/user-role.dto';
import {CreateUserDto} from 'src/app/dtos/user/create-user.dto';
import {AuthenticationService} from 'src/app/services/authentication/authentication.service';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';
import {UserService} from 'src/app/services/user/user.service';
import {PasswordConfirmationValidator} from '../validators/password-confirmation.validator';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent implements OnInit {
  userForm: FormGroup;
  submitted = false;
  hidePassword = true;
  hideConfirmPassword = true;

  availableRoles: Set<string> = new Set<string>();

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly snackBarService: SnackBarService,
    private readonly userService: UserService,
    private readonly passwordConfirmationValidator: PasswordConfirmationValidator,
  ) {
    this.userForm = this.formBuilder.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required, Validators.minLength(8)]],
        userRole: [UserRole.roleUser, [Validators.required]],
      }, {
        validators: [this.passwordConfirmationValidator
          .matchPasswordsRequired('password', 'confirmPassword')]
      }
    );
  }

  ngOnInit(): void {
    this.determineAvailableRoles();
  }

  public createUser() {
    if (this.userForm.valid) {
      const user = new CreateUserDto(
        this.userForm.get('email').value,
        this.userForm.get('password').value,
        this.userForm.get('userRole').value
      );

      this.userService.createUser(user)
        .subscribe({
          next: () => {
            this.router.navigate(['/administration']);
            this.snackBarService.openSuccessSnackbar('User was successfully created!');
          },
          error: error => this.snackBarService.openErrorSnackbar(error)
        });
    }
  }

  private determineAvailableRoles() {
    const currentRole = this.authenticationService.getUserRole();

    if (currentRole === UserRole.roleSuperAdmin) {
      this.availableRoles.add(UserRole.roleAdmin);
      this.availableRoles.add(UserRole.roleUser);
    }

    if (currentRole === UserRole.roleAdmin) {
      this.availableRoles.add(UserRole.roleUser);
    }
  }
}
