export class LocationDto {
  constructor(
    public name: string,
    public street: string,
    public postalCode: number,
    public town: string,
    public country: string
  ) {
  }
}
