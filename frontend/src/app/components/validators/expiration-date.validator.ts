import {FormControl} from '@angular/forms';

export class ExpirationDateValidator {

  static validExpirationDate(control: FormControl): { [key: string]: any } {

    if (!control.value) {
      return null;
    }
    const date = new Date();
    const mm = date.getMonth() + 1;
    let today = '';
    if (mm < 10) {
      today += `0${mm}`;
    }
    today += `/01/${date.getFullYear().toString()}`;
    const expirationDate = new Date(`${control.value.substring(0, 2)}/01/
    ${date.getFullYear().toString().substring(0, 2)}${control.value.slice(-2)}`);
    const currentMonthDate = new Date(today);

    if (expirationDate < currentMonthDate) {
      return {cardExpired: true};
    }
    return null;
  }

}
