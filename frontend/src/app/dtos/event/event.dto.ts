import {Showing} from './showing.dto';

export class EventDto {
  constructor(
    public title: string,
    public categoryId: number,
    public artistIds: number[],
    public description: string,
    public duration: {
      hours: number;
      minutes: number;
    },
    public showings: Showing[],
    public imageRef: string,
    public id?: number) {
  }
}
