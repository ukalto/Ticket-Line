export class EventShowingFilterDto {
  constructor(
    public eventTitle: string,
    public locationName: string,
    public date: Date,
    public startTime: string,
    public endTime: string,
    public minPrice: string,
    public maxPrice: string
  ) {}
}
