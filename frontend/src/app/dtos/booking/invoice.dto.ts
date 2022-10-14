export interface InvoiceLineDto {
    readonly eventTitle: string;
    readonly quantity: number;
    readonly location: string;
    readonly room: string;
    readonly sector: string;
    readonly type: 'seating' | 'standing';
    readonly pricePerUnit: number;
}

export interface InvoiceDto {
    readonly invoiceNumber: number;
    readonly companyLocation: string;
    readonly taxNumber: string;
    readonly invoiceDate: Date;
    readonly bookingId: number;
    readonly invoiceType: 'purchase' | 'cancellation';
    readonly cancelledInvoiceId?: number;
    readonly netSum: number;
    readonly vatAmount: number;
    readonly grossSum: number;
    readonly footerText: string;
    readonly lines: InvoiceLineDto[];
}
