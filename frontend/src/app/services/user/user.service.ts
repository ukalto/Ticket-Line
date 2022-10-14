import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {UserRole} from 'src/app/dtos/authentication/user-role.dto';
import {RegisterCustomerDto} from 'src/app/dtos/customer/register-customer.dto';
import {CreateUserDto} from 'src/app/dtos/user/create-user.dto';
import {Globals} from 'src/app/global/globals';
import {EditUserDto} from '../../dtos/user/edit-user.dto';
import {EditUserAccessDto} from '../../dtos/user/edit-user-access.dto';
import {EditUserPaymentDto} from '../../dtos/user/edit-user-payment.dto';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private customerBaseUri: string = this.globals.backendUri + '/customers';
  private administratorBaseUri: string = this.globals.backendUri + '/administrators';
  private userBaseUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Creates a user with specified credentials.
   *
   * @param userToCreate CreateUserDto that holds the credentials info
   */
  public createUser(userToCreate: CreateUserDto): Observable<any> {
    if (userToCreate.type === UserRole.roleUser) {
      return this.createCustomer(new RegisterCustomerDto(userToCreate.email, userToCreate.password));
    }

    return this.createAdministrator(userToCreate);
  }

  /**
   * Creates an administrator.
   *
   * @param registrationRequest CreateUserDto that holds the new admins credentials info
   */
  public createAdministrator(registrationRequest: CreateUserDto): Observable<CreateUserDto> {
    return this.httpClient.post<CreateUserDto>(this.administratorBaseUri, registrationRequest);
  }

  /**
   * Creates a customer.
   *
   * @param registrationRequest CreateUserDto that holds the new customers credentials info
   */
  public createCustomer(registrationRequest: RegisterCustomerDto): Observable<RegisterCustomerDto> {
    return this.httpClient.post<RegisterCustomerDto>(this.customerBaseUri, registrationRequest);
  }

  /**
   * Finds user by specific email.
   *
   * @param email email to be searched after
   */
  findUserByEmail(email: string): Observable<EditUserDto> {
    return this.httpClient.get<EditUserDto>(`${this.userBaseUri}?email=${email}`);
  }

  /**
   * Updates the users access information (password/email)
   *
   * @param id identifier of the specific user
   * @param editAccess EditUserAccessDto that holds the new access information
   */
  updateUserAccess(id: number, editAccess: EditUserAccessDto): Observable<string> {
    return this.httpClient.patch(`${this.userBaseUri}/${id}/access`, editAccess, {responseType: 'text'});
  }

  /**
   * Updates the users payment information (credit card)
   *
   * @param id identifier of the specific user
   * @param editPayment EditUserPaymentDto that holds the new payment information
   * @param removePayment flag to remove payment
   */
  updateUserPayment(id: number, editPayment: EditUserPaymentDto, removePayment: boolean): Observable<EditUserPaymentDto> {
    return this.httpClient.patch<EditUserPaymentDto>(`${this.userBaseUri}/${id}/payment?removePayment=${removePayment}`
      , editPayment);
  }

  hasPaymentInfo(id: number): Observable<boolean> {
    return this.httpClient.get<void>(this.userBaseUri + '/' + id + '/payment', {observe: 'response'}).pipe(
      map(response => response.status !== 402)
    );
  }

  /**
   * Delete user and reservations of the user
   *
   * @param id unique identifier for user
   */
  deleteUser(id: number) {
    return this.httpClient.delete(`${this.userBaseUri}/${id}`);
  }
}
