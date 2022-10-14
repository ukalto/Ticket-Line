import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookingShowingInfoComponent } from './booking-showing-info.component';

describe('BookingShowingInfoComponent', () => {
  let component: BookingShowingInfoComponent;
  let fixture: ComponentFixture<BookingShowingInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BookingShowingInfoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookingShowingInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
