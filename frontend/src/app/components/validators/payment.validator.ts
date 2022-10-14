import {FormGroup, ValidatorFn} from '@angular/forms';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PaymentValidator {

  public filledOutProperly(ownerKey: string, numberKey: string,
                           expirationDateKey: string, cvvKey: string): ValidatorFn {
    return (formGroup: FormGroup) => {
      const owner = formGroup.controls[ownerKey];
      const number = formGroup.controls[numberKey];
      const expirationDate = formGroup.controls[expirationDateKey];
      const cvv = formGroup.controls[cvvKey];
      let result = null;

      if (!(owner.value || number.value || expirationDate.value || cvv.value)) {
        result =  {noChanges: true};
      }

      if ((!owner.value || !number.value || !expirationDate.value || !cvv.value)) {
        if (result != null) {
          return {notFilledOutProperly: true, noChanges: true};
        }
        return {notFilledOutProperly: true};
      }
      return null;
    };
  }

}
