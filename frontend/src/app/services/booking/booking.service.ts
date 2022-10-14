import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../global/globals';
import {ReservationDto} from '../../dtos/booking/reservation.dto';
import {Observable} from 'rxjs';
import {BookingDto} from '../../dtos/booking/booking.dto';
import {PurchaseDto} from '../../dtos/booking/purchase.dto';
import {BookingOverviewDto} from 'src/app/dtos/booking/booking-overview.dto';
import {ShowingDetails} from 'src/app/dtos/booking/showing-details.dto';
import {ReservationInfoDto} from '../../dtos/booking/reservation-info.dto';
import {ReservationPurchaseDto} from '../../dtos/booking/reservation-purchase.dto';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private bookingBaseUri: string = this.globals.backendUri + '/showing';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Creates booking
   *
   * @param reservationDto all information about reservation being made
   * @param eventShowingId showing being reserved
   */
  reserve(reservationDto: ReservationDto, eventShowingId: number): Observable<BookingDto> {
    const uri = this.bookingBaseUri + '/' + eventShowingId + '/reservation';
    return this.httpClient.post<BookingDto>(uri, reservationDto);
  }

  /**
   * Creates booking and invoice
   *
   * @param purchaseDto all information about purchase being made
   * @param eventShowingId showing being purchased
   */
  purchase(purchaseDto: PurchaseDto, eventShowingId: number): Observable<BookingDto> {
    const uri = this.bookingBaseUri + '/' + eventShowingId + '/purchase';
    return this.httpClient.post<BookingDto>(uri, purchaseDto);
  }

  /**
   * Cancels a booking with bookingId.
   *
   * @param bookingId id of the booking
   */
  cancel(bookingId: number): Observable<BookingOverviewDto> {
    return this.httpClient.patch<BookingOverviewDto>(this.globals.backendUri + '/booking/' + bookingId + '/cancellation', bookingId);
  }

  /**
   * Gets all relevant information about a prior made reservation
   *
   * @param bookingId of reservation
   */
  getReservationInfo(bookingId: number): Observable<ReservationInfoDto> {
    const uri = this.globals.backendUri + '/booking/' + bookingId + '/reservation-info';
    return this.httpClient.get<ReservationInfoDto>(uri);
  }

  /**
   * Creates an invoice for an already existing booking (turns reservation into purchase)
   *
   * @param bookingId of already existing reservation
   * @param reservationPurchaseDto consisting of purchase information
   */
  reservationToPurchase(bookingId: number, reservationPurchaseDto: ReservationPurchaseDto): Observable<BookingDto> {
    const uri = this.globals.backendUri + '/booking/' + bookingId + '/reservation-to-purchase';
    return this.httpClient.post<BookingDto>(uri, reservationPurchaseDto);
  }

  /**
   * Gets bookings of user with id.
   *
   * @param id id of the user
   */
  public getBookingsByUser(id: number): Observable<BookingOverviewDto[]> {
    return this.httpClient.get<BookingOverviewDto[]>(this.globals.backendUri + '/user/' + id + '/bookings');
  }

  /**
   * Gets details of the showing with this id.
   *
   * @param showingId the id of the showing
   * @returns details about the showing
   */
  public getShowingDetails(showingId: number): Observable<ShowingDetails> {
    const uri = this.globals.backendUri + '/showing/' + showingId + '/details';
    return this.httpClient.get<ShowingDetails>(uri);
  }
}
