package pl.asku.askumagazineservice.helpers.data;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.ReviewService;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;

import java.util.Random;

@Service
@AllArgsConstructor
public class ReviewDataProvider {

    ReviewService reviewService;
    ReviewConverter reviewConverter;

    public ReviewDto reviewDto(Reservation reservation) {
        Random random = new Random();
        return ReviewDto.builder()
                .body("test review")
                .rating(random.nextInt(4) + 1)
                .reservationId(reservation.getId())
                .build();
    }

    public Review review(Reservation reservation) throws ReservationNotFoundException, ReviewAlreadyExistsException {
        return reviewService.addReview(reviewConverter.toReview(reviewDto(reservation)));
    }
}
