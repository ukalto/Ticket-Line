import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePasswordEmailComponent } from './update-password-email.component';

describe('UpdatePasswordComponent', () => {
  let component: UpdatePasswordEmailComponent;
  let fixture: ComponentFixture<UpdatePasswordEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdatePasswordEmailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatePasswordEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
