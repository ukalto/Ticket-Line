import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

export interface ChartEntry {
  name: string;
  value: number;
  extra: {
    id: number;
  };
}

@Component({
  selector: 'app-top-ten-events-chart',
  templateUrl: './top-ten-events-chart.component.html',
  styleUrls: ['./top-ten-events-chart.component.scss']
})
export class TopTenEventsChartComponent implements OnInit {
  @Input()
  data: ChartEntry[] = [];

  view: any[] = [800, 600];
  colorScheme = {
    domain: ['#509EE733']
  };

  constructor(
    private readonly router: Router
  ) {
  }

  ngOnInit(): void {
  }

  onSelect(eventId) {
    if (eventId.value) {
      this.router.navigate(['/events/' + eventId.extra.id]);
    } else {
      this.router.navigate(['/events/' + this.data.find(value => value.name === eventId).extra.id]);
    }
  }
}
