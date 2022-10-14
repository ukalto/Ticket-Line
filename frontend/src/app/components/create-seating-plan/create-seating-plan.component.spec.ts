import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSeatingPlanComponent } from './create-seating-plan.component';

describe('CreateSeatingPlanComponent', () => {
  let component: CreateSeatingPlanComponent;
  let fixture: ComponentFixture<CreateSeatingPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateSeatingPlanComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateSeatingPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
