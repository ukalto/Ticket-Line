package at.ac.tuwien.sepm.groupphase.backend.service.user.password.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.user.password.PasswordGenerator;
import java.time.LocalTime;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {
  private final Random random;

  // ASCII: 'a'
  private static final int LOWER_BOUND = 97;

  // ASCII: 'z'
  private static final int UPPER_BOUND = 122;

  // Minimum password length
  private static final int LENGTH = 8;

  public PasswordGeneratorImpl() {
    final var seed = LocalTime.now().toNanoOfDay();
    this.random = new Random(seed);
  }

  @Override
  public String generate() {
    return this.random
        .ints(LOWER_BOUND, UPPER_BOUND + 1)
        .limit(LENGTH)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
