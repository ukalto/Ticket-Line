export class EditUserAccessDto {
  constructor(
    public email: string,
    public currentPassword: string,
    public password: string
  ) {
  }
}
