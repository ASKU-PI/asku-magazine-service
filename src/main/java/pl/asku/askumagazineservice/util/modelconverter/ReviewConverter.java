package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.review.ReviewDto;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;

@Service
@AllArgsConstructor
public class ReviewConverter {

  private final ReservationConverter reservationConverter;

  public Review toReview(ReviewDto reviewDto, Reservation reservation) {
    return Review.builder()
        .id(reviewDto.getId())
        .reservation(reservation)
        .rating(reviewDto.getRating())
        .body(reviewDto.getBody())
        .build();
  }

  public ReviewDto toDto(Review review) {
    return ReviewDto.builder()
        .id(review.getId())
        .body(review.getBody())
        .rating(review.getRating())
        .createdDate(review.getCreatedDate())
        .reservationDto(reservationConverter.toDto(review.getReservation()))
        .build();
  }

  public Review updateReview(Review review, ReviewDto reviewDto) {
    review.setBody(reviewDto.getBody());
    review.setRating(reviewDto.getRating());

    return review;
  }
}
