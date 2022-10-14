import {FormControl} from '@angular/forms';
import {countries} from '../data/countries';

export class CountryValidator {

  static validCountry(control: FormControl): { [key: string]: any } {

    if (!control.value) {
      return null;
    }
    if (!countries.includes(control.value)) {
      return {countryInvalid: true};
    }
    return null;
  }
}
