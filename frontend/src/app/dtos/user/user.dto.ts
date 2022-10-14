export class UserDto {
  constructor(
    public id: number,
    public email: string,
    public isBlocked: boolean,
    public type: string
  ) {
  }
}
