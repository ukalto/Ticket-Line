import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {LocationDto} from '../../dtos/location/location.dto';
import {LocationService} from '../../services/location/location.service';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {countries} from '../data/countries';
import {debounceTime, distinctUntilChanged, Observable} from 'rxjs';
import {startWith, map} from 'rxjs/operators';
import {CountryValidator} from '../validators/country.validator';

@Component({
  selector: 'app-create-location',
  templateUrl: './create-location.component.html',
  styleUrls: ['./create-location.component.scss']
})
export class CreateLocationComponent implements OnInit {
  locationForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  filteredCountries: Observable<string[]>;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private locationService: LocationService,
    private snackBarService: SnackBarService) {
    this.locationForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.pattern('^[A-Za-zäÄöÖüÜß]+[A-Za-zäÄöÖüÜß ]+$')]],
      street: ['', [Validators.required, Validators.pattern('^(?!\s*$).+')]],
      postalCode: ['', [Validators.required, Validators.pattern('^\\d{4,}$')]],
      town: ['', [Validators.required, Validators.pattern('^[A-Za-zäÄöÖüÜß]+[A-Za-zäÄöÖüÜß ]+$')]],
      country: ['', [Validators.required, CountryValidator.validCountry]]
    });

    this.filteredCountries = this.locationForm.get('country').valueChanges.pipe(
      startWith(''),
      debounceTime(400),
      distinctUntilChanged(),
      map(country => (country ? this._filterCountries(country) : countries.slice(0, 5))),
    );
  }

  ngOnInit(): void {
  }

  /**
   * Form validation will start after the method is called, additionally a request for location creation will be sent
   */
  createLocation() {
    this.submitted = true;
    if (this.locationForm.valid && countries.includes(this.locationForm.controls.country.value)) {
      const locReq: LocationDto = new LocationDto(this.locationForm.controls.name.value, this.locationForm.controls.street.value,
        this.locationForm.controls.postalCode.value, this.locationForm.controls.town.value, this.locationForm.controls.country.value);
      this.saveUser(locReq);
    }
  }

  saveUser(locReq: LocationDto) {
    this.locationService.createLocation(locReq).subscribe({
      next: () => {
        this.router.navigate(['/administration']);
        this.snackBarService.openSuccessSnackbar('Location was successfully created!');
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  private _filterCountries(value: string): string[] {
    const filterValue = value.toLowerCase();
    return countries
      .filter(country => country.toLowerCase().includes(filterValue)).slice(0, 5);
  }
}
