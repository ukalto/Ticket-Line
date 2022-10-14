import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatingPlanVisualizationComponent } from './seating-plan-visualization.component';

describe('SeatingPlanVisualizationComponent', () => {
  let component: SeatingPlanVisualizationComponent;
  let fixture: ComponentFixture<SeatingPlanVisualizationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SeatingPlanVisualizationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SeatingPlanVisualizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
