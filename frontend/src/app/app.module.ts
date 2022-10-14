import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {LoginComponent} from './components/login/login.component';
import {NewsEntryComponent} from './components/news-entry/news-entry.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatTabsModule} from '@angular/material/tabs';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatInputModule} from '@angular/material/input';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatSelectModule} from '@angular/material/select';
import {MatMenuModule} from '@angular/material/menu';
import {MatFormFieldModule} from '@angular/material/form-field';
import {RegisterComponent} from './components/register/register.component';
import {CreateNewsEntryComponent} from './components/create-news-entry/create-news-entry.component';
import {CreateEventComponent} from './components/create-event/create-event.component';
import {MatTableModule} from '@angular/material/table';
import {NgxMatFileInputModule} from '@angular-material-components/file-input';
import {
  NGX_MAT_DATE_FORMATS,
  NgxMatDateFormats,
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule
} from '@angular-material-components/datetime-picker';
import {TopTenEventsChartComponent} from './components/top-ten-events-chart/top-ten-events-chart.component';
import {BarChartModule} from '@swimlane/ngx-charts';
import {BookingProcessComponent} from './components/booking-process/booking-process.component';
import {SeatSelectionComponent} from './components/booking-process/seat-selection/seat-selection.component';
import {ConcludeBookingComponent} from './components/booking-process/conclude-booking/conclude-booking.component';
import {
  BookingConfirmationComponent
} from './components/booking-process/booking-confirmation/booking-confirmation.component';
import {MatStepperModule} from '@angular/material/stepper';
import {BookingShowingInfoComponent} from './components/booking-showing-info/booking-showing-info.component';
import {PurchaseComponent} from './components/booking-process/conclude-booking/purchase/purchase.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatRadioModule} from '@angular/material/radio';
import {PriceInfoComponent} from './components/booking-process/price-info/price-info.component';
import {CreateLocationComponent} from './components/create-location/create-location.component';
import {AdministrationComponent} from './components/administration/administration.component';
import {CreateUserComponent} from './components/create-user/create-user.component';
import {CreateArtistComponent} from './components/create-artist/create-artist.component';
import {MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule} from '@angular/material/paginator';
import {HeadlineComponent} from './components/headline/headline.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {UpdateAccountComponent} from './components/update-account/update-account.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {
  UpdatePasswordEmailComponent
} from './components/update-account/update-password-email/update-password-email.component';
import {UpdatePaymentComponent} from './components/update-account/update-payment/update-payment.component';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {NgxMaterialTimepickerModule} from 'ngx-material-timepicker';
import {CreateSeatingPlanComponent} from './components/create-seating-plan/create-seating-plan.component';
import {
  SeatingPlanVisualizationComponent
} from './components/seating-plan-visualization/seating-plan-visualization.component';
import {MatChipsModule} from '@angular/material/chips';
import {MatTooltipModule} from '@angular/material/tooltip';
import {EventsComponent} from './components/events/events.component';
import {LocationsComponent} from './components/locations/locations.component';
import {ArtistsComponent} from './components/artists/artists.component';
import {ShowingsComponent} from './components/showings/showings.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';
import {BookingOverviewComponent} from './components/booking-overview/booking-overview.component';
import {InvoiceComponent} from './components/invoice/invoice.component';
import {ReservationToPurchaseComponent} from './components/reservation-to-purchase/reservation-to-purchase.component';
import {WarningDialogComponent} from './components/warning-dialog/warning-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {TicketComponent} from './components/ticket/ticket.component';
import {QRCodeModule} from 'angularx-qrcode';
import {DatePipe} from '@angular/common';
import {NewsComponent} from './components/news/news.component';
import {NewsEntryItemComponent} from './components/news/news-entry-item/news-entry-item.component';

const INTL_DATE_INPUT_FORMAT = {
  year: 'numeric',
  month: 'numeric',
  day: 'numeric',
  hourCycle: 'h23',
  hour: '2-digit',
  minute: '2-digit',
};

const CUSTOM_DATE_FORMATS: NgxMatDateFormats = {
  parse: {
    dateInput: INTL_DATE_INPUT_FORMAT
  },
  display: {
    dateInput: INTL_DATE_INPUT_FORMAT,
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  }
};

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    CreateSeatingPlanComponent,
    SeatingPlanVisualizationComponent,
    RegisterComponent,
    NewsEntryComponent,
    CreateNewsEntryComponent,
    CreateEventComponent,
    CreateLocationComponent,
    CreateUserComponent,
    AdministrationComponent,
    CreateArtistComponent,
    HeadlineComponent,
    ResetPasswordComponent,
    BookingProcessComponent,
    SeatSelectionComponent,
    ConcludeBookingComponent,
    BookingConfirmationComponent,
    BookingShowingInfoComponent,
    UpdateAccountComponent,
    UpdatePasswordEmailComponent,
    UpdatePaymentComponent,
    TopTenEventsChartComponent,
    LocationsComponent,
    EventsComponent,
    ArtistsComponent,
    ShowingsComponent,
    EventDetailsComponent,
    PurchaseComponent,
    PriceInfoComponent,
    BookingOverviewComponent,
    InvoiceComponent,
    ReservationToPurchaseComponent,
    WarningDialogComponent,
    NewsComponent,
    TicketComponent,
    NewsEntryItemComponent,
  ],
  imports: [
    MatFormFieldModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatCardModule,
    MatDividerModule,
    MatSnackBarModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule,
    MatStepperModule,
    MatGridListModule,
    MatTabsModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatTableModule,
    MatChipsModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSortModule,
    MatPaginatorModule,
    NgxMatFileInputModule,
    NgxMatDatetimePickerModule,
    NgxMatNativeDateModule,
    MatSortModule,
    NgxMaterialTimepickerModule,
    MatDatepickerModule,
    BarChartModule,
    MatPaginatorModule,
    MatChipsModule,
    MatTooltipModule,
    MatButtonToggleModule,
    MatCheckboxModule,
    MatExpansionModule,
    MatRadioModule,
    MatDialogModule,
    QRCodeModule,
  ],
  providers: [
    {provide: NGX_MAT_DATE_FORMATS, useValue: CUSTOM_DATE_FORMATS},
    DatePipe,
    httpInterceptorProviders,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
