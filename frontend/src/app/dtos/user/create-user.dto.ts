import {UserRole} from '../authentication/user-role.dto';

export class CreateUserDto {
  constructor(
    public readonly email: string,
    public readonly password: string,
    public readonly type: UserRole
  ) {
  }
}
