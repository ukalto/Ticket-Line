export class NewsEntry {
  constructor(
    public title: string,
    public contents: string,
    public summary: string,
    public publishedBy: number,
    public imageRef: string,
    public eventId: number,
    public id?: number,
    public publishedOn?: number) {
  }
}
