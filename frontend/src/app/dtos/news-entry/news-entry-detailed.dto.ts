export class NewsEntryDetailed {
  constructor(
    public id: number,
    public title: string,
    public date: string,
    public contents: string,
    public eventId: number,
    public file: any
  ) {
  }
}
