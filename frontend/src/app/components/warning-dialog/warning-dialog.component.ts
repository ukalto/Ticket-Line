import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-warning-dialog',
  templateUrl: './warning-dialog.component.html',
  styleUrls: ['./warning-dialog.component.scss']
})
export class WarningDialogComponent {

  title: string;
  text: string;
  note: string;
  submitButton: string;

  constructor(public dialogRef: MatDialogRef<boolean>,
    @Inject(MAT_DIALOG_DATA) public data: {title: string; text: string; note: string; submitButton: string}) {
    this.title = data.title;
    this.text = data.text;
    this.submitButton = data.submitButton;
    this.note = data.note;
  }

}
