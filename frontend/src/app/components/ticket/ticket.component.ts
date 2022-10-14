import { AfterViewInit, Component, ElementRef, Input, ViewChild } from '@angular/core';
import { TicketDto } from 'src/app/dtos/tickets/ticket.dto';
import html2pdf from 'html2pdf.js';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-ticket',
  templateUrl: './ticket.component.html',
  styleUrls: ['./ticket.component.scss']
})
export class TicketComponent implements AfterViewInit {
  @ViewChild('tickets')
  public ticketsElem!: ElementRef;

  @Input()
  public ticketsInput: TicketDto[];

  constructor(public datepipe: DatePipe) { }

  async ngAfterViewInit(): Promise<void> {
    // give the DOM some time render
    // so html2pdf doesn't create blank QR codes...
    setTimeout(async () => {
      const ticketsNativeElem = this.ticketsElem.nativeElement;

      const clonedTicketsElem = ticketsNativeElem.cloneNode(true);

      clonedTicketsElem.style.display = 'block';
      await html2pdf(clonedTicketsElem, {filename: `ticket_${this.datepipe
          .transform(this.ticketsInput[0].startingTime, 'yyyy-MM-dd')}`});
    }, 1 * 1000);
  }

  renderDate(dateString?: string): string {
    if (!dateString) {
      return '';
    }

    return this.datepipe.transform(dateString,'dd.MM.yyyy');
  }

  renderTime(dateString?: string): string {
    if (!dateString) {
      return '';
    }

    const date = new Date(dateString);
    return `${date.getUTCHours()+2}:${date.getUTCMinutes()}${date.getUTCMinutes() / 10 === 0 ? '0' : ''}`;
  }
}
