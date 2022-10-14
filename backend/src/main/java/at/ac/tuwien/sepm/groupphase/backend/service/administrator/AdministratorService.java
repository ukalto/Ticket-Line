package at.ac.tuwien.sepm.groupphase.backend.service.administrator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.administrator.CreateAdministratorDto;

public interface AdministratorService {
  /**
   * Creates a new administrator.
   *
   * @param administratorDto contains the data of the administrator to be created.
   */
  CreateAdministratorDto create(CreateAdministratorDto administratorDto);
}
