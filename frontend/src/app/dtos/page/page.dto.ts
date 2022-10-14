export class PageDto<T> {
  constructor(
    public totalElements: number,
    public content: Array<T>,
  ) {
  }
}
