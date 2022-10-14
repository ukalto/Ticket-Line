import {Component, Input, OnInit} from '@angular/core';
import {NewsEntryOverview} from 'src/app/dtos/news-entry/news-entry-overview.dto';

@Component({
  selector: 'app-news-entry-item',
  templateUrl: './news-entry-item.component.html',
  styleUrls: ['./news-entry-item.component.scss']
})
export class NewsEntryItemComponent implements OnInit {

  @Input() newsEntry: NewsEntryOverview;
  @Input() unread = false;

  constructor() {
  }

  ngOnInit(): void {
  }
}
