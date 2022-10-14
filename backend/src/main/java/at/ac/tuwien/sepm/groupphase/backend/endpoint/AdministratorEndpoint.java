package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.administrator.CreateAdministratorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.administrator.CreateAdministratorResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.service.administrator.AdministratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.invoke.MethodHandles;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AdministratorEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final AdministratorService administratorService;

  @Secured("ROLE_SUPER_ADMINISTRATOR")
  @PostMapping("/administrators")
  @Operation(
      summary = "Creates a new administrator user.",
      security = @SecurityRequirement(name = "apiKey"))
  public CreateAdministratorResponseDto create(@RequestBody CreateAdministratorDto administrator) {
    LOGGER.info("POST /api/v1/administrators created: {}", administrator);
    this.administratorService.create(administrator);
    return new CreateAdministratorResponseDto(administrator.email());
  }
}
