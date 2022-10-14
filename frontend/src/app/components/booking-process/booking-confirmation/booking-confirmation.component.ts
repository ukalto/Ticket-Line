import {Component, Input, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs';
import {BookingType} from 'src/app/dtos/booking/booking-type';
import {ReservationDto} from 'src/app/dtos/booking/reservation.dto';
import { InvoiceService } from 'src/app/services/invoice/invoice.service';
import { SnackBarService } from 'src/app/services/snackbar/snack-bar.service';

@Component({
  selector: 'app-booking-confirmation',
  templateUrl: './booking-confirmation.component.html',
  styleUrls: ['./booking-confirmation.component.scss']
})
export class BookingConfirmationComponent implements OnInit {
  @ViewChild('pdfs', { read: ViewContainerRef }) pdfs: ViewContainerRef;

  @Input() eventId: number;
  @Input() showingId: number;
  @Input() bookingType: BookingType;
  @Input() bookingInfo: ReservationDto;
  @Input() bookingId: number;

  bookingTypes = BookingType;

  constructor(
    private readonly invoiceService: InvoiceService,
    private readonly snackbarService: SnackBarService,
    private readonly activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    if (!this.bookingId) {
      this.activatedRoute.params.subscribe({
        next: params => {
          this.bookingId = params.bookingId;
        },
        error: error => this.snackbarService.openErrorSnackbar(error)
      });
    }
  }

  getTitleText() {
    if (!this.bookingType) {
      return;
    }

    let typeText: string = this.bookingType;
    typeText = typeText.charAt(0).toUpperCase() + typeText.slice(1);

    return typeText + ' successful!';
  }

  public onPrintInvoiceClick() {
   this.invoiceService
    .findPurchaseInvoiceForBooking(this.bookingId)
    .subscribe({
      next: (invoice) => {
        this.invoiceService.printInvoiceAsPdf(invoice, this.pdfs);
      },
      error: (error) => this.snackbarService.openErrorSnackbar(error)
    });
  }

  public onPrintTicketsClick() {
   this.invoiceService
    .findPurchaseInvoiceForBooking(this.bookingId)
    .pipe(
      switchMap(invoice => this.invoiceService.ticketsForInvoiceId(invoice.invoiceNumber))
    )
    .subscribe({
      next: (tickets) => {
        this.invoiceService.printTicketsAsPdf(tickets, this.pdfs);
      },
      error: (error) => this.snackbarService.openErrorSnackbar(error)
    });
  }
}
