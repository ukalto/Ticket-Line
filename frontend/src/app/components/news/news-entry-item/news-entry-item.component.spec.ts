import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsEntryItemComponent } from './news-entry-item.component';

describe('NewsEntryItemComponent', () => {
  let component: NewsEntryItemComponent;
  let fixture: ComponentFixture<NewsEntryItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewsEntryItemComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewsEntryItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
