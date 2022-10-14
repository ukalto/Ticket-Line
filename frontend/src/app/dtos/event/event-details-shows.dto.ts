export class EventDetailsShowsDto {
  constructor(
    public id: number,
    public eventId: number,
    public eventTitle: string,
    public date: string,
    public time: string,
    public location: string,
    public room: string,
    public lowestPrice: number,
    public highestPrice: number,
    public bookedOut: boolean
  ) {}
}
