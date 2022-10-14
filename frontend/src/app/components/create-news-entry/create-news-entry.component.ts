import {Component, OnInit} from '@angular/core';
import {NewsEntryService} from '../../services/news-entry/news-entry.service';
import {NewsEntry} from 'src/app/dtos/news-entry/news-entry.dto';
import {Router} from '@angular/router';
import {EventService} from 'src/app/services/event/event.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Location} from '@angular/common';
import {AuthenticationService} from 'src/app/services/authentication/authentication.service';
import {UserService} from 'src/app/services/user/user.service';
import {EventDto} from 'src/app/dtos/event/event.dto';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';
import {EditUserDto} from 'src/app/dtos/user/edit-user.dto';
import { ElementRef } from '@angular/core';
import { ViewChild } from '@angular/core';


@Component({
  selector: 'app-create-news-entry',
  templateUrl: './create-news-entry.component.html',
  styleUrls: ['./create-news-entry.component.scss']
})
export class CreateNewsEntryComponent implements OnInit {

  @ViewChild('uploadButton', { read: ElementRef }) uploadButton: ElementRef<HTMLElement>;

  events: EventDto[];
  createNewsForm: FormGroup;
  loading = false;

  constructor(private newsEntryService: NewsEntryService,
              private eventService: EventService,
              private userService: UserService,
              private authService: AuthenticationService,
              private location: Location,
              private snackBarService: SnackBarService,
              private router: Router,
  ) {
    this.createNewsForm = new FormGroup({
      title: new FormControl('', [Validators.required, Validators.minLength(2), Validators.pattern('^[0-9A-Za-zäÄöÖüÜß!?. ]+$')]),
      contents: new FormControl('', [Validators.required, Validators.minLength(10)]),
      summary: new FormControl(''),
      imageRef: new FormControl(''),
      eventId: new FormControl('', [Validators.required]),
    });
  }

  ngOnInit(): void {
    this.getAvailableEvents();
  }

  createNewsEntry(newsEntry: NewsEntry) {
    this.newsEntryService.createNewsEntry(newsEntry).subscribe({
        next: newsEntryResponse => {
          if (this.createNewsForm.value.imageRef !== '') {
            this.newsEntryService.saveImage(newsEntryResponse.id, this.createNewsForm.value.imageRef).subscribe({
              error: error => {
                this.snackBarService.openErrorSnackbar('Could save image: ' + error);
              }
            });
          }
          //redirect to admin page
          this.router.navigate(['/administration']);
          this.snackBarService.openSuccessSnackbar('News Entry successfully created!');
        },
        error: error => {
          this.snackBarService.openErrorSnackbar('Could not create news entry: ' + error);
        }
      }
    );
  }

  redirectFileUploadClick() {
    const buttonElement: HTMLElement = this.uploadButton.nativeElement.children[1].children[0] as HTMLElement;
    buttonElement.click();
  }

  getAvailableEvents() {
    this.eventService.getEvents().subscribe({
      next: (events: EventDto[]) => {
        this.events = events;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar('Could not fetch available events: ' + error);
      }
    });
  }

  onSubmit() {
    if (this.createNewsForm.value.imageRef.size/1024/1024 >= 5) {
      this.snackBarService.openErrorSnackbar('The image you selected is too large. The maximum allowed file size is 5 MB.');
      return;
    }
    if (this.createNewsForm.valid === false) {
      return;
    }
    this.userService.findUserByEmail(this.authService.getUserName()).subscribe({
      next: (user: EditUserDto) => {
        const newsEntry = new NewsEntry(
          this.createNewsForm.value.title,
          this.createNewsForm.value.contents,
          this.createNewsForm.value.summary,
          user.id,
          this.createNewsForm.value.imageRef.name,
          this.createNewsForm.value.eventId);
        this.createNewsEntry(newsEntry);
      },
      error: error => {
        //implement error
        this.snackBarService.openErrorSnackbar('There was a problem with your user: ' + error);
      }
    });
  }

  goBack() {
    this.location.back();
  }
}
