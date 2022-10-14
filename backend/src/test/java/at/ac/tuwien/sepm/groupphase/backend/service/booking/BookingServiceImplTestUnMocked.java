package at.ac.tuwien.sepm.groupphase.backend.service.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.BookingOverviewDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BookingServiceImplTestUnMocked {
  @Autowired private BookingService bookingService;

  @Test
  void cancelBooking_shouldWork() {
    bookingService.cancelBooking(-1L);
    List<BookingOverviewDto> bookings = bookingService.findAllBookingsByBookedBy(-1L);
    assertEquals(bookings.size(), 0);
  }
}
