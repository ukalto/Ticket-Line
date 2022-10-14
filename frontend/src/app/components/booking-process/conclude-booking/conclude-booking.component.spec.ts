import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConcludeBookingComponent } from './conclude-booking.component';

describe('ConcludeBookingComponent', () => {
  let component: ConcludeBookingComponent;
  let fixture: ComponentFixture<ConcludeBookingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConcludeBookingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConcludeBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
