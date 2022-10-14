export class ArtistDto {
  constructor(
    public firstName: string,
    public lastName: string,
    public artistName: string,
    public id?: number,
  ) {
  }

  public toString(): string {
    if (this.artistName === '') {
      return `First and Last Name: ${this.firstName} ${this.lastName}`;
    }
    return `Artist name: ${this.artistName}`;
  }
}
