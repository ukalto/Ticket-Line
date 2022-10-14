import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateNewsEntryComponent } from './create-news-entry.component';

describe('CreateNewsEntryComponent', () => {
  let component: CreateNewsEntryComponent;
  let fixture: ComponentFixture<CreateNewsEntryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateNewsEntryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateNewsEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
