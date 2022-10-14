import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackBarService} from '../../services/snackbar/snack-bar.service';
import {ArtistService} from '../../services/artist/artist.service';
import {ArtistEventDto} from '../../dtos/artist/artist-event.dto';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-artists',
  templateUrl: './artists.component.html',
  styleUrls: ['./artists.component.scss']
})
export class ArtistsComponent implements OnInit {

  filterArtists: FormGroup;
  resetFilterOff = false;
  artistEvents: ArtistEventDto[] = [];
  pageIndex = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private artistService: ArtistService,
              private snackBarService: SnackBarService,
              private _sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.pageIndex = params.page? params.page : 0;
      this.pageSize = params.size? params.size : 10;
      this.resetFilterOff = Object.keys(params).length === 0 ||
        Object.keys(params).length === 2 && this.pageIndex > 0;
      this.filterArtists = this.formBuilder.group({
        name: [params.name],
      });
    });
    this.applyFilter(true);
  }

  applyFilter(loadMore = false) {
    if(!loadMore) {
      this.pageIndex = 0;
    }
    const queryParams: any = {};
    // avoid seeing empty queryParams
    if (this.filterArtists.controls.name.value) {
      queryParams.name = this.filterArtists.controls.name.value;
    }

    if(this.pageIndex > 0){
      if(this.artistEvents.length === 0){
        queryParams.page = 0;
        queryParams.size = this.pageIndex*10+10;
      }else {
        queryParams.page = this.pageIndex;
        queryParams.size = this.pageSize;
      }
    }

    this.artistService.filterArtistEvents(queryParams).subscribe({
      next: data => {
        if(loadMore) {
          this.artistEvents.push(...data.content);
        }else {
          this.artistEvents = data.content;
        }
        this.totalElements = data.totalElements;

      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });

    if(this.pageIndex > 0){
      queryParams.page = this.pageIndex;
      queryParams.size = this.pageSize;
    }

    this.router.navigate([], {queryParams});
  }

  resetFilter() {
    this.filterArtists.reset();
    this.applyFilter();
  }

  detailsAndBooking(id) {
    this.router.navigate([`/events/${id}`]);
  }

  extractImage(file: string): SafeResourceUrl {
    return this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + file);
  }

  loadMore() {
    this.pageIndex++;
    this.applyFilter(true);
  }

}
