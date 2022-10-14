import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ArtistDto} from '../../dtos/artist/artist.dto';
import {Router} from '@angular/router';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {ArtistService} from '../../services/artist/artist.service';
import {MatTabChangeEvent} from '@angular/material/tabs';

@Component({
  selector: 'app-create-artist',
  templateUrl: './create-artist.component.html',
  styleUrls: ['./create-artist.component.scss']
})
export class CreateArtistComponent implements OnInit {

  artistForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  nameSelector: any;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private artistService: ArtistService,
    private snackBarService: SnackBarService) {
    this.artistForm = this.formBuilder.group({
      firstName: ['', [Validators.pattern('^[A-Za-zäÄöÖüÜß]+$')]],
      lastName: ['', [Validators.pattern('^[A-Za-zäÄöÖüÜß]+$')]],
      artistName: ['']
    });
  }

  ngOnInit(): void {
    this.nameSelector = 'artistNameSelector';
  }

  public onSelectorSwitch(value: any) {
    this.nameSelector = value;
    if (value === 'artistNameSelector') {
      this.artistForm.patchValue({
        firstName: '',
        lastName: ''
      });
    } else {
      this.artistForm.patchValue({
        artistName: ''
      });
    }
  }

  submitArtist() {
    this.submitted = true;
    if (this.nameSelector === 'artistNameSelector') {
      if (this.artistForm.controls.artistName.value === '') {
        this.snackBarService.openErrorSnackbar('Fill in the artist name.');
      } else {
        this.createArtist(new ArtistDto('', '', this.artistForm.controls.artistName.value));
      }
    } else if (this.nameSelector === 'fullNameSelector') {
      if (this.artistForm.controls.firstName.value.trim() === '' || this.artistForm.controls.lastName.value.trim() === '') {
        this.snackBarService.openErrorSnackbar('Fill in first and last name.');
      } else if (!this.artistForm.controls.firstName.value.match('^[A-Za-zäÄöÖüÜß]+$') ||
        !this.artistForm.controls.lastName.value.match('^[A-Za-zäÄöÖüÜß]+$')) {
        this.snackBarService.openErrorSnackbar('Invalid input');
      } else {
        this.createArtist(new ArtistDto(this.artistForm.controls.firstName.value, this.artistForm.controls.lastName.value, ''));
      }
    }
  }

  tabChange(event: MatTabChangeEvent): void {
    if(event.index === 0) {
      this.nameSelector = 'artistNameSelector';
    }else {
      this.nameSelector = 'fullNameSelector';
    }
  }

  private createArtist(artReq: ArtistDto) {
    this.artistService.createArtist(artReq).subscribe({
      next: () => {
        this.router.navigate(['/administration']);
        this.snackBarService.openSuccessSnackbar(`Artist was successfully created: ${artReq.toString()}`);
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }
}
