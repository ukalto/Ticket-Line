import {Component, OnInit} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {NewsEntryOverview} from 'src/app/dtos/news-entry/news-entry-overview.dto';
import {AuthenticationService} from 'src/app/services/authentication/authentication.service';
import {NewsEntryService} from 'src/app/services/news-entry/news-entry.service';
import {SnackBarService} from 'src/app/services/snackbar/snack-bar.service';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {

  newsEntries: NewsEntryOverview[] = [];
  readNewsEntries: NewsEntryOverview[] = [];
  gotReadNews = false;

  constructor(private newsEntryService: NewsEntryService,
              private snackBarService: SnackBarService,
              private authenticationService: AuthenticationService,
              private router: Router,
              private _sanitizer: DomSanitizer) {
  }


  ngOnInit(): void {
    this.getNews();
  }

  getNews() {
    this.newsEntryService.getNews().subscribe({
      next: newsEntries => {
        for (const newsEntry of newsEntries) {
          newsEntry.imageRef = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + newsEntry.imageRef);
        }
        this.newsEntries = newsEntries;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  getReadNews() {
    if (this.gotReadNews === false) {
      this.newsEntryService.getReadNews().subscribe({
        next: newsEntries => {
          for (const newsEntry of newsEntries) {
            newsEntry.imageRef = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + newsEntry.imageRef);
          }
          this.readNewsEntries = newsEntries;
          this.gotReadNews = true;
        },
        error: error => {
          this.snackBarService.openErrorSnackbar(error);
        }
      });
    }
  }

  isLoggedIn() {
    return this.authenticationService.isLoggedIn();
  }
}
