import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../../../services/authentication/authentication.service';
import {ExpirationDateValidator} from '../../../validators/expiration-date.validator';
import {EditUserDto} from '../../../../dtos/user/edit-user.dto';
import {SnackBarService} from '../../../../services/snackbar/snack-bar.service';
import {PaymentValidator} from '../../../validators/payment.validator';
import {UserService} from '../../../../services/user/user.service';
import {MatRadioChange} from '@angular/material/radio';
import {BookingService} from '../../../../services/booking/booking.service';
import {PurchaseDto} from '../../../../dtos/booking/purchase.dto';
import {ReservationDto, Seat, NonSeat} from '../../../../dtos/booking/reservation.dto';
import {PaymentInfoDto} from '../../../../dtos/booking/payment-info.dto';
import {ReservationPurchaseDto} from '../../../../dtos/booking/reservation-purchase.dto';

@Component({
  selector: 'app-purchase',
  templateUrl: './purchase.component.html',
  styleUrls: ['./purchase.component.scss']
})
export class PurchaseComponent implements OnInit {

  @Input() bookingId: number;

  @Input() bookingInfo: ReservationDto;
  @Input() showingId: number;
  @Input() editUserDto: EditUserDto;

  @Output() submitPurchase = new EventEmitter<number>();
  @Output() submitError = new EventEmitter();

  paymentForm: FormGroup;

  paymentInfoExists = true;
  panelExpanded = true;
  savedPaymentSelected: boolean = null;
  newPaymentTitle = 'Payment Information';
  hideCvv = true;
  hideCardNumber = true;


  constructor(private authenticationService: AuthenticationService,
              private formBuilder: FormBuilder,
              private snackBarService: SnackBarService,
              private paymentValidator: PaymentValidator,
              private bookingService: BookingService,
              private userService: UserService) {

    this.paymentForm = this.formBuilder.group({
        cardOwner: ['', Validators.required],
        cardNumber: ['', [Validators.required, Validators.pattern('^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9]' +
          '[0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|' + '(?:2131|1800|35\\d{3})\\d{11})$')]],
        cardExpirationDate: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])/[0-9]{2}$'),
          ExpirationDateValidator.validExpirationDate]],
        cardCvv: ['', [Validators.required, Validators.pattern('^([0-9]{3,4})$')]],
        savePayment: false
      }
    );
  }

  ngOnInit(): void {
    this.userService.hasPaymentInfo(this.bookingInfo.bookedBy).subscribe({
        next: (data) => {
          this.paymentInfoExists = data;

          if (this.paymentInfoExists) {
            this.panelExpanded = false;
            this.newPaymentTitle = 'New Payment Information';
          }
        },
        error: (message) => {
          this.paymentInfoExists = message.status !== 404;
        }
      }
    );
  }

  formatString(event) {
    String.fromCharCode(event.keyCode);
    const code = event.keyCode;
    const allowedKeys = [8];
    if (allowedKeys.indexOf(code) !== -1) {
      return;
    }

    event.target.value = event.target.value.replace(
      /^([1-9]\/|[2-9])$/g, '0$1/' // 3 > 03/
    ).replace(
      /^(0[1-9]|1[0-2])$/g, '$1/' // 11 > 11/
    ).replace(
      /^([0-1])([3-9])$/g, '0$1/$2' // 13 > 01/3
    ).replace(
      /^(0?[1-9]|1[0-2])([0-9]{2})$/g, '$1/$2' // 141 > 01/41
    ).replace(
      /^([0]+)\/|[0]+$/g, '0' // 0/ > 0 and 00 > 0
    ).replace(
      /[^\d\/]|^[\/]*$/g, '' // To allow only digits and `/`
    ).replace(
      /\/\//g, '/' // Prevent entering more than 1 `/`
    );
  }

  numberOnly(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    return !(charCode > 31 && (charCode < 48 || charCode > 57));
  }

  confirmPurchase() {
    if (this.bookingId) {
      this.confirmReservationToPurchase();
      return;
    }

    // adjust indices of seats
    const adjustedBookingInfo = this.adjustIndicesOfBookingInfo();

    // do purchase
    this.paymentForm.controls['cardOwner'].markAsTouched();
    this.paymentForm.controls['cardNumber'].markAsTouched();
    this.paymentForm.controls['cardExpirationDate'].markAsTouched();
    this.paymentForm.controls['cardCvv'].markAsTouched();

    const purchaseDto = new PurchaseDto(adjustedBookingInfo, null, null, null, null);

    if ((!this.paymentInfoExists || this.savedPaymentSelected === false) && this.paymentForm.valid) {
      purchaseDto.cardOwner = this.paymentForm.controls['cardOwner'].value;
      purchaseDto.cardNumber = this.paymentForm.controls['cardNumber'].value;
      purchaseDto.cardExpirationDate = this.paymentForm.controls['cardExpirationDate'].value;
      purchaseDto.cardCvv = this.paymentForm.controls['cardCvv'].value;
      this.bookingService.purchase(purchaseDto, this.showingId).subscribe({
        next: (data) => {
          this.submitPurchase.emit(data.bookingId);
        },
        error: (message) =>{
          this.snackBarService.openErrorSnackbar(message);
          this.submitError.emit();
        }
      });

      if (this.paymentForm.controls['savePayment'].value === true) {
        this.savePaymentInformation();
      }
      return;
    }

    if (this.savedPaymentSelected === true) {
      this.bookingService.purchase(purchaseDto, this.showingId).subscribe({
        next: (data) => {
          this.submitPurchase.emit(data.bookingId);
        },
        error: (message) =>{
          this.snackBarService.openErrorSnackbar(message);
          this.submitError.emit();
        }
      });
      return;
    }
  }

  confirmReservationToPurchase() {
    this.paymentForm.controls['cardOwner'].markAsTouched();
    this.paymentForm.controls['cardNumber'].markAsTouched();
    this.paymentForm.controls['cardExpirationDate'].markAsTouched();
    this.paymentForm.controls['cardCvv'].markAsTouched();

    const adjustedBookingInfo = this.adjustIndicesOfBookingInfo();

    const paymentDto = new PaymentInfoDto(null, null, null, null);
    const reservationPurchaseDto = new ReservationPurchaseDto(
      adjustedBookingInfo.seatingPlanId,
      adjustedBookingInfo.bookedSeats,
      adjustedBookingInfo.bookedNonSeats,
      adjustedBookingInfo.cost,
      paymentDto);

    if ((!this.paymentInfoExists || this.savedPaymentSelected === false) && this.paymentForm.valid) {
      paymentDto.cardOwner = this.paymentForm.controls['cardOwner'].value;
      paymentDto.cardNumber = this.paymentForm.controls['cardNumber'].value;
      paymentDto.cardExpirationDate = this.paymentForm.controls['cardExpirationDate'].value;
      paymentDto.cardCvv = this.paymentForm.controls['cardCvv'].value;
      reservationPurchaseDto.paymentInfoDto = paymentDto;
      this.bookingService.reservationToPurchase(this.bookingId, reservationPurchaseDto).subscribe({
        next: (data) => {
          this.submitPurchase.emit(data.bookingId);
        },
        error: (message) =>{
          this.snackBarService.openErrorSnackbar(message);
          this.submitError.emit();
        }
      });

      if (this.paymentForm.controls['savePayment'].value === true) {
        this.savePaymentInformation();
      }
      return;
    }

    if (this.savedPaymentSelected === true) {
      this.bookingService.reservationToPurchase(this.bookingId, reservationPurchaseDto).subscribe({
        next: (data) => {

          this.submitPurchase.emit(data.bookingId);
        },
        error: (message) =>{
          this.snackBarService.openErrorSnackbar(message);
          this.submitError.emit();
        }
      });
      return;
    }
  }

  adjustIndicesOfBookingInfo(): ReservationDto {
    const adjustedBookingInfo = new ReservationDto(this.bookingInfo.bookedBy, this.bookingInfo.seatingPlanId,
      this.bookingInfo.cost, [], []);
    this.bookingInfo.bookedSeats.forEach(element => {
      adjustedBookingInfo.bookedSeats.push(new Seat(element.seat + 1, element.row + 1, element.sector + 1));
    });
    this.bookingInfo.bookedNonSeats.forEach(element => {
      adjustedBookingInfo.bookedNonSeats.push(new NonSeat(element.sector + 1, element.amount));
    });
    return adjustedBookingInfo;
  }

  savePaymentInformation(): void {
    const editUserDto: EditUserDto = new EditUserDto(
      this.bookingInfo.bookedBy,
      'email',
      this.paymentForm.controls['cardOwner'].value,
      this.paymentForm.controls['cardNumber'].value,
      this.paymentForm.controls['cardExpirationDate'].value,
      this.paymentForm.controls['cardOwner'].value);

    this.userService.updateUserPayment(this.bookingInfo.bookedBy, editUserDto, false).subscribe({
      next: () => {},
      error: error => this.snackBarService.openErrorSnackbar(error)
    });
  }

  expandPanel(radioButton: MatRadioChange): void {
    if (radioButton.value.toString() === 'savedPayment') {
      this.panelExpanded = false;
      this.savedPaymentSelected = true;
    } else {
      this.panelExpanded = true;
      this.savedPaymentSelected = false;
    }
  }
}

