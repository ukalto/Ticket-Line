import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { UserDto } from '../../dtos/user/user.dto';
import { MatSort } from '@angular/material/sort';
import { debounceTime, distinctUntilChanged, filter, fromEvent } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AdministrationService } from '../../services/administration/administration.service';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { SnackBarService } from '../../services/snackbar/snack-bar.service';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss']
})
export class AdministrationComponent implements AfterViewInit {

  @ViewChild('input', { static: true }) input: ElementRef;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  dataSource: MatTableDataSource<UserDto>;
  pageIndex = 0;
  pageSize = 10;
  pageSizeOptions = [5, 10, 25, 100];
  displayedColumns: string[] = ['email', 'type', 'isBlocked', 'resetPassword'];

  constructor(
    private administrationService: AdministrationService,
    private authenticationService: AuthenticationService,
    private snackBarService: SnackBarService
  ) {
    this.dataSource = new MatTableDataSource<UserDto>([]);
    this.getUsers('');
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    fromEvent(this.input.nativeElement,
      'keyup').pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((event: KeyboardEvent) => {
          this.pageIndex = 0;
          this.getUsers(this.input.nativeElement.value);
        })
      ).subscribe();
  }

  refreshTable() {
    this.getUsers(this.input.nativeElement.value);
  }

  updateIsBlocked(row: UserDto, isBlocked: boolean): void {
    this.administrationService.updateUserIsBlocked(row.id, this.authenticationService.getUserRole(), isBlocked).subscribe({
      next: data => {
        // index changes when table header sort is active
        const index = this.dataSource.data.indexOf(row);
        this.dataSource.data[index].isBlocked = data.isBlocked;
        // refresh
        this.dataSource.data = this.dataSource.data;
      },
      error: error => {
        this.snackBarService.openErrorSnackbar(error);
      }
    });
  }

  public resetPassword(row: UserDto) {
    this.administrationService.triggerPasswordReset(row.id)
      .subscribe({
        next: () => this.snackBarService.openSuccessSnackbar('Sent password reset mail.'),
        error: (error) => this.snackBarService.openErrorSnackbar(error)
      });
  }

  changePaginator(event) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.refreshTable();
  }

  private getUsers(input: string): void {
    this.administrationService.findUsersByEmail(input, this.authenticationService.getUserRole(),
      this.pageIndex, this.pageSize).subscribe({
        next: data => {
          if (data != null) {
            const emptySize = data.totalElements - (this.pageSize * (this.pageIndex + 1));
            data.content.push(...new Array<UserDto>(emptySize < 0 ? 0 : emptySize));
            this.dataSource.data.splice(this.pageIndex * this.pageSize,
              this.dataSource.data.length, ...data.content);
            this.dataSource.data = this.dataSource.data;
          } else {
            this.dataSource.data = null;
          }
        },
        error: error => {
          this.snackBarService.openErrorSnackbar(error);
          if (error.status !== 401) {
            this.getUsers('');
          }
        }
      });
  }
}
