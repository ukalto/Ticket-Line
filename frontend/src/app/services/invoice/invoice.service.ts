import { HttpClient } from '@angular/common/http';
import { ComponentRef, Injectable, ViewContainerRef } from '@angular/core';
import {  Observable, switchMap } from 'rxjs';
import { InvoiceComponent } from '../../components/invoice/invoice.component';
import { TicketComponent } from '../../components/ticket/ticket.component';
import { InvoiceDto } from '../../dtos/booking/invoice.dto';
import { TicketDto } from '../../dtos/tickets/ticket.dto';
import { Globals } from '../../global/globals';

interface SimpleInvoiceDto {
  invoiceNumber: number;
  invoiceType: 'purchase' | 'cancellation';
}

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private invoiceBaseUri: string = this.globals.backendUri + '/invoice';

  constructor(
    private readonly httpClient: HttpClient,
    private readonly globals: Globals,
  ) {}

  public findById(invoiceId: number): Observable<InvoiceDto> {
    return this.httpClient.get<InvoiceDto>(`${this.invoiceBaseUri}/${invoiceId}`);
  }

  public findPurchaseInvoiceForBooking(bookingId: number): Observable<InvoiceDto> {
    return this.httpClient
      .get<SimpleInvoiceDto[]>(`${this.globals.backendUri}/booking/${bookingId}/invoices`)
      .pipe(
        switchMap(simpleInvoices => {
          const purchaseInvoice = simpleInvoices.find((simpleInvoice) => simpleInvoice.invoiceType === 'purchase');

          return this.findById(purchaseInvoice.invoiceNumber);
        })
      );
  }

  public printInvoiceAsPdf(invoice: InvoiceDto, viewContainerRef: ViewContainerRef): ComponentRef<InvoiceComponent> {
    const component = viewContainerRef.createComponent(InvoiceComponent);

    component.instance.invoiceDto = invoice;

    return component;
  }

  public ticketsForInvoiceId(invoiceId: number): Observable<Array<TicketDto>> {
    return this.httpClient.get<TicketDto[]>(`${this.globals.backendUri}/tickets/${invoiceId}`);
  }

  public printTicketsAsPdf(tickets: TicketDto[], viewContainerRef: ViewContainerRef): ComponentRef<TicketComponent> {
    const component = viewContainerRef.createComponent(TicketComponent);

    component.instance.ticketsInput = tickets;

    return component;
  }
}
