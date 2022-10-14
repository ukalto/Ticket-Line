package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "at.ac.tuwien.sepm.groupphase.backend.entity.Invoice")
@Table(name = "invoice")
public class Invoice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "invoice_number", nullable = false)
  private Long invoiceNumber;

  @Column(name = "purchased_at", nullable = false)
  private Timestamp purchasedAt;

  @Column(name = "booking_id", nullable = false)
  private Long bookingId;

  @Column(name = "invoice_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private InvoiceType invoiceType;
}
