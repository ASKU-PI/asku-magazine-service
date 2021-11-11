package pl.asku.askumagazineservice.repository;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.review.Review;

public interface ReviewRepository extends PagingAndSortingRepository<Review, Long> {
  Optional<Review> findByReservation_Id(Long id);

  Integer countByReservation_Magazine_Id(Long magazineId);

  Integer countByReservation_User_Id(String userId);

  @Query(
      "SELECT ROUND(AVG(rw.rating), 1) FROM Review rw JOIN Reservation rn ON rn.id = rw"
          + ".reservation.id JOIN Magazine m ON m.id = rn.magazine.id WHERE m.id = :magazineId")
  BigDecimal averageByReservation_Magazine_Id(Long magazineId);

  @Query(
      "SELECT ROUND(AVG(rw.rating), 1) FROM Review rw JOIN Reservation rn ON rn.id = rw"
          + ".reservation.id JOIN User u ON u.id = rn.user.id WHERE u.id = :userId")
  BigDecimal averageByReservation_User_Id(String userId);

  Page<Review> findAllByReservation_Magazine_Id(Long magazineId, Pageable pageRequest);
}
