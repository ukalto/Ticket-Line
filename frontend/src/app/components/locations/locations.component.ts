import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {map, startWith} from 'rxjs/operators';
import {debounceTime, distinctUntilChanged, Observable} from 'rxjs';
import {countries} from '../data/countries';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {LocationService} from '../../services/location/location.service';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {LocationEventDto} from '../../dtos/location/location-event.dto';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-location',
  templateUrl: './locations.component.html',
  styleUrls: ['./locations.component.scss']
})

export class LocationsComponent implements OnInit {

  filterLocations: FormGroup;
  filteredCountries: Observable<string[]>;
  resetFilterOff = false;
  locationEvents: LocationEventDto[] = [];
  pageIndex = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              public authenticationService: AuthenticationService,
              private locationService: LocationService,
              private snackBarService: SnackBarService,
              private _sanitizer: DomSanitizer) {

    this.route.queryParams.subscribe(params => {
      this.pageIndex = params.page? params.page : 0;
      this.pageSize = params.size? params.size : 10;
      this.resetFilterOff = Object.keys(params).length === 0 ||
        Object.keys(params).length === 2 && this.pageIndex > 0;
      this.filterLocations = formBuilder.group({
        name: [params.name],
        street: [params.street],
        country: [params.country],
        town: [params.town],
        postalCode: [params.postalCode],
      });
    });
    this.addLocationFilter();
    this.applyFilter(true);
  }

  ngOnInit(): void {
  }

  addLocationFilter() {
    this.filteredCountries = this.filterLocations.get('country').valueChanges.pipe(
      startWith(''),
      debounceTime(400),
      distinctUntilChanged(),
      map(country => (country ? this._filterCountries(country) : countries.slice(0, 5))),
    );
  }

  applyFilter(loadMore = false) {
    if(!loadMore) {
      this.pageIndex = 0;
    }
    const queryParams: any = {};
    // avoid seeing empty queryParams
    if (this.filterLocations.controls.name.value) {
      queryParams.name = this.filterLocations.controls.name.value;
    }
    if (this.filterLocations.controls.street.value) {
      queryParams.street = this.filterLocations.controls.street.value;
    }
    if (this.filterLocations.controls.country.value) {
      queryParams.country = this.filterLocations.controls.country.value;
    }
    if (this.filterLocations.controls.town.value) {
      queryParams.town = this.filterLocations.controls.town.value;
    }
    if (this.filterLocations.controls.postalCode.value) {
      queryParams.postalCode = this.filterLocations.controls.postalCode.value;
    }

    if(this.pageIndex > 0){
      if(this.locationEvents.length === 0){
        queryParams.page = 0;
        queryParams.size = this.pageIndex*10+10;
      }else {
        queryParams.page = this.pageIndex;
        queryParams.size = this.pageSize;
      }
    }


    this.locationService.filterLocationEvents(queryParams).subscribe({
      next: data => {
        if(loadMore) {
          this.locationEvents.push(...data.content);
        }else {
          this.locationEvents = data.content;
        }
        this.totalElements = data.totalElements;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });

    if(this.pageIndex > 0){
      queryParams.page = this.pageIndex;
      queryParams.size = this.pageSize;
    }

    this.router.navigate([], {queryParams}).then(r => this.addLocationFilter());
  }

  resetFilter() {
    this.filterLocations.reset();
    this.applyFilter();
  }

  detailsAndBooking(id) {
    this.router.navigate([`/events/${id}`]);
  }

  extractImage(file: string): SafeResourceUrl {
    return this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + file);
  }

  loadMore() {
    this.pageIndex++;
    this.applyFilter(true);
  }

  private _filterCountries(value: string): string[] {
    const filterValue = value.toLowerCase();
    return countries
      .filter(country => country.toLowerCase().includes(filterValue)).slice(0, 5);
  }

}
