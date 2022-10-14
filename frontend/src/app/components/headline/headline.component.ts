import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-headline',
  templateUrl: './headline.component.html',
  styleUrls: ['./headline.component.scss']
})
export class HeadlineComponent implements OnInit {

  @Input() text = 'Sample title';
  @Input() icon = 'home';
  @Input() centered = false;

  constructor() { }

  ngOnInit(): void {
  }

}
