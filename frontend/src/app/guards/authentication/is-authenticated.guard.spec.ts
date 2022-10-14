import { inject, TestBed } from '@angular/core/testing';

import { IsAuthenticatedGuard } from './is-authenticated.guard';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';

describe('AuthGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
      providers: [IsAuthenticatedGuard]
    });
  });

  it('should ...', inject([IsAuthenticatedGuard], (guard: IsAuthenticatedGuard) => {
    expect(guard).toBeTruthy();
  }));
});
