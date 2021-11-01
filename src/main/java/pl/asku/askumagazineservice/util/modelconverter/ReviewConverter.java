package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.util.LocalDateConverter;

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
}
