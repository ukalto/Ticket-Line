package at.ac.tuwien.sepm.groupphase.backend.service.customer;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.customer.RegisterCustomerDto;

public interface CustomerService {
  /**
   * Creates a new customer.
   *
   * @param customerDto containing all the data required to register.
   * @return the provided data to register.
   */
  RegisterCustomerDto registerCustomer(RegisterCustomerDto customerDto);
}
