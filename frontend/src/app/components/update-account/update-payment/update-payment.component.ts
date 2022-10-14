import {Component, Input} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../../services/authentication/authentication.service';
import {ExpirationDateValidator} from '../../validators/expiration-date.validator';
import {EditUserDto} from '../../../dtos/user/edit-user.dto';
import {SnackBarService} from '../../../services/snackbar/snack-bar.service';
import {PaymentValidator} from '../../validators/payment.validator';
import {EditUserPaymentDto} from '../../../dtos/user/edit-user-payment.dto';
import {UserService} from '../../../services/user/user.service';
import {Location} from '@angular/common';

@Component({
  selector: 'app-update-payment',
  templateUrl: './update-payment.component.html',
  styleUrls: ['./update-payment.component.scss']
})
export class UpdatePaymentComponent {

  @Input() editUserDto: EditUserDto;
  // Flag for credit card input (true = all input must be given, false = otherwise)
  @Input() groupInput = true;
  updatePaymentForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  hideCvv = true;
  hideNumber = true;

  constructor(private authenticationService: AuthenticationService,
              private formBuilder: FormBuilder,
              private snackBarService: SnackBarService,
              private paymentValidator: PaymentValidator,
              private userService: UserService,
              private location: Location) {

    this.updatePaymentForm = this.formBuilder.group({
        cardOwner: [''],
        cardNumber: ['', [Validators.pattern('^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9]' +
          '[0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|' + '(?:2131|1800|35\\d{3})\\d{11})$')]],
        cardExpirationDate: ['', [Validators.pattern('^(0[1-9]|1[0-2])/[0-9]{2}$'),
          ExpirationDateValidator.validExpirationDate]],
        cardCvv: ['', Validators.pattern('^([0-9]{3,4})$')]
      }, {
        validators: [this.paymentValidator
          .filledOutProperly('cardOwner', 'cardNumber'
            , 'cardExpirationDate', 'cardCvv')]
      }
    );
  }

  back() {
    this.location.back();
  }

  formatString(event) {
    String.fromCharCode(event.keyCode);
    const code = event.keyCode;
    const allowedKeys = [8];
    if (allowedKeys.indexOf(code) !== -1) {
      return;
    }

    event.target.value = event.target.value.replace(
      /^([1-9]\/|[2-9])$/g, '0$1/' // 3 > 03/
    ).replace(
      /^(0[1-9]|1[0-2])$/g, '$1/' // 11 > 11/
    ).replace(
      /^([0-1])([3-9])$/g, '0$1/$2' // 13 > 01/3
    ).replace(
      /^(0?[1-9]|1[0-2])([0-9]{2})$/g, '$1/$2' // 141 > 01/41
    ).replace(
      /^([0]+)\/|[0]+$/g, '0' // 0/ > 0 and 00 > 0
    ).replace(
      /[^\d\/]|^[\/]*$/g, '' // To allow only digits and `/`
    ).replace(
      /\/\//g, '/' // Prevent entering more than 1 `/`
    );
  }

  numberOnly(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    return !(charCode > 31 && (charCode < 48 || charCode > 57));
  }

  updatePayment(removePayment: boolean): void {
    this.submitted = true;

    // Check if a validator is invalid (not abstract)
    for (const el in this.updatePaymentForm.controls) {
      if (this.updatePaymentForm.controls[el].errors) {
        return;
      }
    }

    // Check if abstract validator is invalid
    if (!removePayment && this.updatePaymentForm.errors &&
      ((this.groupInput && this.updatePaymentForm.errors.notFilledOutProperly)
        || (!this.groupInput && this.updatePaymentForm.errors.noChanges != null && this.updatePaymentForm.errors.noChanges))) {
      return;
    }

    this.userService.updateUserPayment(this.editUserDto.id,
      new EditUserPaymentDto(this.updatePaymentForm.controls.cardOwner.value,
        this.updatePaymentForm.controls.cardNumber.value, this.updatePaymentForm.controls.cardExpirationDate.value,
        this.updatePaymentForm.controls.cardCvv.value), removePayment).subscribe({
      next: data => {
        this.groupInput = removePayment;
        this.submitted = false;
        this.updatePaymentForm.reset();
        this.editUserDto.cardOwner = data.cardOwner;
        this.editUserDto.cardNumber = data.cardNumber;
        this.editUserDto.cardExpirationDate = data.cardExpirationDate;
        this.editUserDto.cardCvv = data.cardCvv;
        this.snackBarService.openSuccessSnackbar('Successfully changed access information!');
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

}
