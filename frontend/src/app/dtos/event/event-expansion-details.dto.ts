export class EventExpansionDetailsDto {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public category: string,
    public imageReference: string,
    public soldOut: boolean
  ) {
  }
}
