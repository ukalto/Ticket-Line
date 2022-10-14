import {BookingProcessComponent} from './components/booking-process/booking-process.component';
import {LocationsComponent} from './components/locations/locations.component';
import {ArtistsComponent} from './components/artists/artists.component';
import {ShowingsComponent} from './components/showings/showings.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {UpdateAccountComponent} from './components/update-account/update-account.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {IsAuthenticatedGuard} from './guards/authentication/is-authenticated.guard';
import {IsNotAuthenticatedGuard} from './guards/authentication/is-not-authenticated.guard';
import {RegisterComponent} from './components/register/register.component';
import {IsAdminGuard} from './guards/administration/is-admin.guard';
import {CreateLocationComponent} from './components/create-location/create-location.component';
import {AdministrationComponent} from './components/administration/administration.component';
import {CreateArtistComponent} from './components/create-artist/create-artist.component';
import {CreateSeatingPlanComponent} from './components/create-seating-plan/create-seating-plan.component';
import {CreateUserComponent} from './components/create-user/create-user.component';
import {EventsComponent} from './components/events/events.component';
import {NewsEntryComponent} from './components/news-entry/news-entry.component';
import {CreateNewsEntryComponent} from './components/create-news-entry/create-news-entry.component';
import {CreateEventComponent} from './components/create-event/create-event.component';
import {BookingOverviewComponent} from './components/booking-overview/booking-overview.component';
import {InvoiceComponent} from './components/invoice/invoice.component';
import {ReservationToPurchaseComponent} from './components/reservation-to-purchase/reservation-to-purchase.component';
import {NewsComponent} from './components/news/news.component';

const routes: Routes = [
  {path: 'booking/:eventId/:showingId', canActivate: [IsAuthenticatedGuard], component: BookingProcessComponent},
  {path: '', redirectTo: '/news', pathMatch: 'full'},
  {path: 'login', canActivate: [IsNotAuthenticatedGuard], component: LoginComponent},
  {path: 'register', canActivate: [IsNotAuthenticatedGuard], component: RegisterComponent},
  {path: 'administration', canActivate: [IsAdminGuard], component: AdministrationComponent},
  {path: 'events', component: EventsComponent},
  {path: 'administration/create-artist', canActivate: [IsAdminGuard], component: CreateArtistComponent},
  {path: 'administration/create-location', canActivate: [IsAdminGuard], component: CreateLocationComponent},
  {path: 'administration/create-news', canActivate: [IsAdminGuard], component: CreateNewsEntryComponent},
  {path: 'administration/create-event', canActivate: [IsAdminGuard], component: CreateEventComponent},
  {path: 'administration/create-seating-plan', canActivate: [IsAdminGuard], component: CreateSeatingPlanComponent},
  {path: 'administration/create-user', canActivate: [IsAdminGuard], component: CreateUserComponent},
  {path: 'edit-account', canActivate: [IsAuthenticatedGuard], component: UpdateAccountComponent},
  {path: 'news', component: NewsComponent},
  {path: 'locations', component: LocationsComponent},
  {path: 'events/:id', component: EventDetailsComponent},
  {path: 'artists', component: ArtistsComponent},
  {path: 'shows', component: ShowingsComponent},
  {path: 'reset-password', canActivate: [], component: ResetPasswordComponent},
  {path: 'booking/:eventId/:showingId', canActivate: [IsAuthenticatedGuard], component: BookingProcessComponent},
  {path: 'news/:id', component: NewsEntryComponent},
  {path: 'booking-overview', component: BookingOverviewComponent},
  {path: 'invoice', component: InvoiceComponent},
  {path: 'purchase-booking/:bookingId', component: ReservationToPurchaseComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
