package at.ac.tuwien.sepm.groupphase.backend.repository.booking;

import java.math.BigInteger;
import java.time.Duration;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DurationConverter implements AttributeConverter<BigInteger, Duration> {
  @Override
  public Duration convertToDatabaseColumn(BigInteger bigInteger) {
    return Duration.ofNanos(bigInteger.longValue());
  }

  @Override
  public BigInteger convertToEntityAttribute(Duration duration) {
    return BigInteger.valueOf(duration.toNanos());
  }
}
