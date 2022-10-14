import {Component, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {BookingOverviewDto} from 'src/app/dtos/booking/booking-overview.dto';
import {BookingState} from 'src/app/dtos/booking/booking-state';
import {AuthenticationService} from 'src/app/services/authentication/authentication.service';
import {BookingService} from 'src/app/services/booking/booking.service';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';
import {UserService} from 'src/app/services/user/user.service';
import {Router} from '@angular/router';
import {InvoiceService} from 'src/app/services/invoice/invoice.service';
import {WarningDialogComponent} from '../warning-dialog/warning-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-booking-overview',
  templateUrl: './booking-overview.component.html',
  styleUrls: ['./booking-overview.component.scss', '../../../styles.scss']
})
export class BookingOverviewComponent implements OnInit {
  @ViewChild('pdfs', {read: ViewContainerRef}) pdfs: ViewContainerRef;

  bookings: BookingOverviewDto[] = [];

  constructor(
    private bookingService: BookingService,
    private authenticationService: AuthenticationService,
    private snackBarService: SnackBarService,
    private userService: UserService,
    private _sanitizer: DomSanitizer,
    private router: Router,
    private readonly invoiceService: InvoiceService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.getBookings();
  }

  determineColor(bookingType: BookingState) {
    if (bookingType === BookingState.paid) {
      return 'success';
    }
    if (bookingType === BookingState.cancelled) {
      return 'warn';
    }
  }

  getBookings() {
    this.userService.findUserByEmail(this.authenticationService.getUserName()).subscribe({
      next: userId => {
        this.bookingService.getBookingsByUser(userId.id).subscribe({
          next: bookings => {
            for (const booking of bookings) {
              booking.file = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + booking.file);
            }
            this.bookings = bookings;
          },
          error: error => {
            this.snackBarService.openErrorSnackbar(error);
          }
        });
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  reservationToPurchase(bookingId: number) {
    this.router.navigate(['purchase-booking/' + bookingId]);
  }

  public printInvoice(invoiceId: number) {
    this.invoiceService
      .findById(invoiceId)
      .subscribe({
        next: invoice => this.invoiceService.printInvoiceAsPdf(invoice, this.pdfs),
        error: error => this.snackBarService.openErrorSnackbar(error)
      });
  }

  public printTickets(invoiceId: number) {
    this.invoiceService
      .ticketsForInvoiceId(invoiceId)
      .subscribe({
        next: tickets => this.invoiceService.printTicketsAsPdf(tickets, this.pdfs),
        error: error => this.snackBarService.openErrorSnackbar(error)
      });
  }

  cancel(bookingId: number) {
    const dialogRef = this.dialog.open(WarningDialogComponent, {
      data: {
        title: `Delete this booking`,
        text: 'Do you really want to delete your Booking permanently?',
        note: 'Your booking will be cancelled permanently and the seats you have selected will be open again.',
        submitButton: `Cancel permanently`
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.bookingService.cancel(bookingId).subscribe({
          next: data => {
            for (let i = 0; i < this.bookings.length; i++) {
              if (this.bookings[i].bookingId === data.bookingId) {
                if (data.bookingState === 'Reserved') {
                  this.bookings.slice(i, 1);
                } else {
                  data.file = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + data.file);
                  this.bookings[i] = data;
                }
                this.getBookings();
                this.snackBarService.openSuccessSnackbar('Successfully cancelled');
                return;
              }
            }
          },
          error: error => {
            this.snackBarService.openErrorSnackbar('Couldn\'t successfully cancel');
          }
        });
      }
    });
  }
}
