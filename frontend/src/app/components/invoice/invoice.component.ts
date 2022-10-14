import { AfterViewInit, Component, ElementRef, Input, ViewChild } from '@angular/core';
import html2pdf from 'html2pdf.js';
import { InvoiceDto } from 'src/app/dtos/booking/invoice.dto';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss']
})
export class InvoiceComponent implements AfterViewInit {
  @ViewChild('invoice')
  public invoice!: ElementRef;

  @Input()
  public invoiceDto: InvoiceDto;

  constructor(public datepipe: DatePipe) {
  }

  async ngAfterViewInit(): Promise<void> {
    const invoiceElem = this.invoice.nativeElement;

    const clonedInvoiceElem = invoiceElem.cloneNode(true);
    clonedInvoiceElem.style.display = 'block';

    await html2pdf(clonedInvoiceElem,{filename: `${
      this.invoiceDto.invoiceType === 'purchase'? 'invoice' : 'cancellation_invoice'}_${this.datepipe
        .transform(this.invoiceDto.invoiceDate, 'yyyy-MM-dd')}`});
  }

  renderDate(dateString?: string): string {
    if (!dateString) {
      return '';
    }

    return this.datepipe.transform(dateString,'dd.MM.yyyy');
  }
}
