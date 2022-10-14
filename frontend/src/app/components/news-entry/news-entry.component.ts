import {Component, OnInit} from '@angular/core';
import {NewsEntryService} from '../../services/news-entry/news-entry.service';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';
import {NewsEntryDetailed} from 'src/app/dtos/news-entry/news-entry-detailed.dto';
import {ActivatedRoute, Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {AuthenticationService} from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-news-entry',
  templateUrl: './news-entry.component.html',
  styleUrls: ['./news-entry.component.scss']
})
export class NewsEntryComponent implements OnInit {

  newsEntry: NewsEntryDetailed;

  constructor(private newsEntryService: NewsEntryService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private snackBarService: SnackBarService,
              private authenticationService: AuthenticationService,
              private _sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.activatedRoute.paramMap.subscribe(params => {
      const newsEntryId = params.get('id');
      this.getNewsEntry(+newsEntryId);
    });
  }

  redirectToEvent() {
    this.router.navigate(['events/' + this.newsEntry.eventId]);
  }


  getNewsEntry(id: number) {
    this.newsEntryService.getNewsEntryById(id).subscribe({
      next: (newsEntry: NewsEntryDetailed) => {
        newsEntry.file = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + newsEntry.file);
        this.newsEntry = newsEntry;
        if (this.authenticationService.isLoggedIn()) {
          this.newsEntryService.registerNewsRead(id).subscribe({
            error: error => {
              this.snackBarService.openErrorSnackbar(error);
            }
          });
        }
      },
      error: error => {
        if (error.error.status === 404) {
          this.router.navigate(['news']).then();
          this.snackBarService.openErrorSnackbar('No existing event with this id!');
        } else {
          this.snackBarService.openErrorSnackbar(error);
        }
      }
    });
  }


}
