import { Injectable } from '@angular/core';
import {AsyncValidatorFn, FormArray, FormGroup} from '@angular/forms';
import { Observable } from 'rxjs';
import { EventService } from 'src/app/services/event/event.service';

@Injectable({ providedIn: 'root' })
export class EventDuplicateBooking {

    public formArray(formGroup: FormGroup, name: string) {
        return formGroup.get(name) as FormArray;
    }

    public isOccupied(rowId: number, seatingPlan: any, eventService: EventService): AsyncValidatorFn {
        return (formGroup: FormGroup) => {
            const startTime = this.formArray(formGroup, 'startTimes').at(rowId).value;
            const endTime = this.formArray(formGroup, 'endTimes').at(rowId).value;
            //var seatingPlanId = seatingPlan.id;

            const observable = new Observable(subscriber => {
                subscriber.next({occupied: true});
                subscriber.complete();
            });

            //replace 1 with id
            eventService.isOccupied(1, startTime, endTime).subscribe({
                next: (occupied: boolean) => {
                    if (occupied) {
return observable;
}
                },
                error: error => {
                    console.error(error);
                }
            });
            return new Observable(subscriber => {
                subscriber.next(null);
                subscriber.complete();
            });
        };
    }
}
