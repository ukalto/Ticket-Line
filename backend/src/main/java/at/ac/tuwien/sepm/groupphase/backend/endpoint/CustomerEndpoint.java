package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.customer.RegisterCustomerDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.customer.RegisterCustomerResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.service.customer.CustomerService;
import javax.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEndpoint.class);
  private final CustomerService customerService;

  @PostMapping("/customer/registration")
  @PermitAll
  public ResponseEntity<RegisterCustomerResponseDto> handleRegistration(
      @RequestBody RegisterCustomerDto customerDto) {
    LOGGER.info("create customer {}", customerDto.email());

    var created = customerService.registerCustomer(customerDto);

    return ResponseEntity.ok(new RegisterCustomerResponseDto(created.email()));
  }

  @PostMapping("/customers")
  @Secured("ROLE_ADMINISTRATOR")
  public ResponseEntity<RegisterCustomerResponseDto> create(
      @RequestBody RegisterCustomerDto customerDto) {
    LOGGER.info("create customer {}", customerDto.email());

    var created = customerService.registerCustomer(customerDto);

    return ResponseEntity.ok(new RegisterCustomerResponseDto(created.email()));
  }
}
