import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../global/globals';
import {RegisterCustomerDto} from 'src/app/dtos/customer/register-customer.dto';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private customerBaseUri: string = this.globals.backendUri + '/customer';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Register user.
   *
   * @param registrationRequest User data
   */
  createCustomer(registrationRequest: RegisterCustomerDto): Observable<RegisterCustomerDto> {
    return this.httpClient
      .post<RegisterCustomerDto>(this.customerBaseUri + '/registration', registrationRequest);
  }
}
