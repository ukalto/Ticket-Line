import {TestBed} from '@angular/core/testing';

import {NewsEntryService} from './news-entry.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {ReactiveFormsModule} from '@angular/forms';

describe('NewsEntryService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
  }));

  it('should be created', () => {
    const service: NewsEntryService = TestBed.inject(NewsEntryService);
    expect(service).toBeTruthy();
  });
});
