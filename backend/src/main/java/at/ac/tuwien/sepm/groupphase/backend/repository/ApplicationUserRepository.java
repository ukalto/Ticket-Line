package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUserType;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

  /**
   * Find an ApplicationUser entry with the given email.
   *
   * @return Optional of non-null or empty (ApplicationUser)
   */
  Optional<ApplicationUser> findByEmail(String email);

  /**
   * Find an ApplicationUser entry with the given id.
   *
   * @param id is of the ApplicationUser that shall be updated.
   * @param roles roles that are allowed to be updated
   * @return Optional of non-null or empty (ApplicationUser)
   */
  Optional<ApplicationUser> findByIdAndTypeIn(Long id, Collection<ApplicationUserType> roles);

  /**
   * Find a Page of ApplicationUser entries with the given role and email.
   *
   * @param roles role type of ApplicationUser
   * @param email email of the user
   * @param pageable defines pagination
   * @return list of Application users
   */
  Page<ApplicationUser> findByTypeInAndEmailContaining(
      @Param("TYPE") Collection<ApplicationUserType> roles,
      @Param("EMAIL") String email,
      Pageable pageable);

  /**
   * Find an ApplicationUser by ID.
   *
   * @param id to search for.
   * @return an Optional containing the ApplicationUser if it exists.
   */
  Optional<ApplicationUser> findById(Long id);

  /**
   * Delete all reservations from user and user.
   *
   * @param id unique identifier of the user
   */
  @Modifying
  @Transactional
  @Query(
      value =
          "DELETE FROM BOOKING_NON_SEAT BNS WHERE BNS.BOOKING_ID IN ( "
              + "SELECT B.ID FROM BOOKING B LEFT JOIN INVOICE I ON I.BOOKING_ID = B.ID "
              + "WHERE B.BOOKED_BY = ?1 AND INVOICE_NUMBER IS NULL); "
              + "DELETE FROM BOOKING_SEAT BS WHERE BS.BOOKING_ID IN ( "
              + "SELECT B.ID FROM BOOKING B LEFT JOIN INVOICE I ON I.BOOKING_ID = B.ID "
              + "WHERE B.BOOKED_BY = ?1 AND INVOICE_NUMBER IS NULL); "
              + "DELETE FROM BOOKING B WHERE B.ID IN ( "
              + "SELECT B2.ID FROM BOOKING B2 LEFT JOIN INVOICE I ON I.BOOKING_ID = B2.ID "
              + "WHERE B2.BOOKED_BY = ?1 AND INVOICE_NUMBER IS NULL); "
              + "DELETE FROM APPLICATION_USER WHERE ID = ?1 AND TYPE = 'ROLE_USER';",
      nativeQuery = true)
  void deleteUserAndReservations(Long id);
}
