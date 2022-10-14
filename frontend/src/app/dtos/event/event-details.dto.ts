export class EventDetailsDto {
  constructor(
    public id: number,
    public title: string,
    public categoryId: number,
    public description: string,
    public file: string
  ) {
  }
}
