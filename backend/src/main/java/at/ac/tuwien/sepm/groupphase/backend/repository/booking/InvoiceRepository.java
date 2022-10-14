package at.ac.tuwien.sepm.groupphase.backend.repository.booking;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  /**
   * Checks whether an invoice exists with a given booking id and invoice type.
   *
   * @param bookingId used for matching booking id's of invoice entries
   * @param invoiceType used for matching invoice types of invoice entries
   * @return true if an invoice exists and false if not
   */
  boolean existsByBookingIdAndInvoiceType(Long bookingId, InvoiceType invoiceType);

  /**
   * Finds Invoice via bookingId and invoiceType if it exists.
   *
   * @param bookingId used for matching booking id's of invoice entries
   * @return Invoice if it exists
   */
  @Query(
      value =
          "SELECT invoice_number FROM Invoice where booking_id =?1 and invoice_type = 'purchase'",
      nativeQuery = true)
  Long findInvoiceNumberByBookingIdAndPurchased(@Param("booking_id") Long bookingId);

  /**
   * Gets a list of purchases with the bookingId.
   *
   * @param bookingId used for matching bookingId in database
   * @return list of purchases
   */
  List<Invoice> findAllByBookingId(Long bookingId);
}
