import { BookingState } from './booking-state';

export class BookingOverviewDto {
    constructor(
        public bookingId: number,
        public invoiceNumber: number,
        public cancellationInvoiceNumber: number,
        public file: any,
        public title: string,
        public date: string,
        public duration: string,
        public ticketCount: number,
        public price: number,
        public bookingState: BookingState,
    ) {}
}
