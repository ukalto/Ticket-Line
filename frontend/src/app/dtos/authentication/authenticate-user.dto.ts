export class AuthenticateUserDto {
  constructor(
    public email: string,
    public password: string
  ) {
  }
}
