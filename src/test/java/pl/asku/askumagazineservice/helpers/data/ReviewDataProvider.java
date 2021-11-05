package pl.asku.askumagazineservice.helpers.data;

import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.review.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;
import pl.asku.askumagazineservice.service.ReviewService;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;

@Service
@AllArgsConstructor
public class ReviewDataProvider {

  ReviewService reviewService;
  ReviewConverter reviewConverter;

  public ReviewDto reviewDto() {
    Random random = new Random();
    return ReviewDto.builder()
        .body("test review")
        .rating(random.nextInt(4) + 1)
        .build();
  }

  public Review review(Reservation reservation)
      throws ReservationNotFoundException, ReviewAlreadyExistsException {
    return reviewService.addReview(reviewDto(), reservation);
  }
}
