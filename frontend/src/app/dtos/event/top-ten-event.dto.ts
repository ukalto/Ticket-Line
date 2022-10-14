export class TopTenEventDto {
  constructor(
    public readonly id: number,
    public readonly title: string,
    public readonly categoryId: string,
    public readonly duration: any,
    public readonly description: string,
    public readonly totalBookings: number
  ) {
  }
}
