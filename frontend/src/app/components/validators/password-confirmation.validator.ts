import { Injectable } from '@angular/core';
import { FormGroup, ValidatorFn } from '@angular/forms';

@Injectable({ providedIn: 'root' })
export class PasswordConfirmationValidator {
    public matchPasswordsRequired(passwordKey: string, passwordConfirmationKey: string): ValidatorFn {
        return (formGroup: FormGroup) => {
            const passwordInput = formGroup.controls[passwordKey];
            const passwordConfirmationInput = formGroup.controls[passwordConfirmationKey];
            if (!passwordInput.value || passwordInput.value !== passwordConfirmationInput.value) {
                const error = { passwordsDoNotMatch: true };
                passwordConfirmationInput.setErrors(error);
                return error;
            }
            // Tells confirmationControl to check if still valid on password
            // field input (would only update on passwordControl here)
            passwordConfirmationInput.updateValueAndValidity({onlySelf: true, emitEvent: false});
            return null;
        };
    }

  public matchPasswordsNotRequired(passwordKey: string, passwordConfirmationKey: string): ValidatorFn {
    return (formGroup: FormGroup) => {
      const passwordInput = formGroup.controls[passwordKey];
      const passwordConfirmationInput = formGroup.controls[passwordConfirmationKey];

      if (passwordInput.value !== passwordConfirmationInput.value) {
        const error = { passwordsDoNotMatch: true };
        passwordConfirmationInput.setErrors(error);
        return error;
      }
      // Tells confirmationControl to check if still valid on password
      // field input (would only update on passwordControl here)
      passwordConfirmationInput.updateValueAndValidity({onlySelf: true, emitEvent: false});
      return null;
    };
  }

  public allFilledOutOrNot(currentPasswordKey: string, passwordKey: string, passwordConfirmationKey: string): ValidatorFn {
    return (formGroup: FormGroup) => {
      const currentPassword = formGroup.controls[currentPasswordKey];
      const password = formGroup.controls[passwordKey];
      const passwordConfirmation = formGroup.controls[passwordConfirmationKey];

      if ((currentPassword.value || password.value || passwordConfirmation.value)
        && (!currentPassword.value || !password.value || !passwordConfirmation.value)) {
        return {notFilledOutProperly: true};
      }
      return null;
    };
  }
}
