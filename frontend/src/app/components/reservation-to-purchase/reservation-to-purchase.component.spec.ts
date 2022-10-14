import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationToPurchaseComponent } from './reservation-to-purchase.component';

describe('ReservationToPurchaseComponent', () => {
  let component: ReservationToPurchaseComponent;
  let fixture: ComponentFixture<ReservationToPurchaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReservationToPurchaseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReservationToPurchaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
