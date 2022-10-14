import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopTenEventsChartComponent } from './top-ten-events-chart.component';

describe('TopTenEventsChartComponent', () => {
  let component: TopTenEventsChartComponent;
  let fixture: ComponentFixture<TopTenEventsChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TopTenEventsChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TopTenEventsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
