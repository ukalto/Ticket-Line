package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

public record EventShowingFilterDto(
    String eventTitle,
    String locationName,
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal minPrice,
    BigDecimal maxPrice) {}
