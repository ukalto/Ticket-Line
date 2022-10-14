export class LocationResponseDto {
  constructor(
    public id: number,
    public name: string,
    public street: string,
    public postalCode: number,
    public town: string,
    public country: string
  ) {
  }
}
