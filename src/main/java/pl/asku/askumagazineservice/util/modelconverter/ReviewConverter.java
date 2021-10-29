package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.ReservationService;

@Service
@AllArgsConstructor
public class ReviewConverter {

    private final ReservationService reservationService;

    public Review toReview(ReviewDto reviewDto) throws ReservationNotFoundException {
        Reservation reservation = reservationService.getReservation(reviewDto.getReservationId());

        return Review.builder()
                .id(reviewDto.getId())
                .reservation(reservation)
                .rating(reviewDto.getRating())
                .body(reviewDto.getBody())
                .build();
    }
}
