package pl.asku.askumagazineservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.reservation.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findByMagazine_Id(Long id);

    List<Reservation> findByMagazine_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long id,
                                                                                           LocalDate startDate,
                                                                                           LocalDate endDate);

    @Query("SELECT r from Reservation r WHERE r.magazine.id = :id AND (r.startDate <= :startDate AND r.endDate >= " +
            ":startDate) OR (r.startDate >= :startDate AND r.startDate <= :endDate)")
    List<Reservation> findActiveReservations(Long id, LocalDate startDate, LocalDate endDate);
}
