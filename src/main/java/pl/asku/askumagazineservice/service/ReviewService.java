package pl.asku.askumagazineservice.service;

import java.math.BigDecimal;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.dto.review.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.exception.ReviewNotFoundException;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;
import pl.asku.askumagazineservice.model.review.ReviewSearchResult;
import pl.asku.askumagazineservice.repository.ReviewRepository;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;

@Service
@Validated
@AllArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewConverter reviewConverter;

  public Review addReview(@Valid ReviewDto reviewDto, @Valid Reservation reservation)
      throws ReviewAlreadyExistsException, ReservationNotFoundException {
    try {
      getReviewByReservationId(reservation.getId());
      throw new ReviewAlreadyExistsException();
    } catch (ReviewNotFoundException e) {
      Review review = reviewConverter.toReview(reviewDto, reservation);
      return reviewRepository.save(review);
    }
  }

  public Review getReview(@NotNull Long id) throws ReviewNotFoundException {
    Optional<Review> review = reviewRepository.findById(id);
    if (review.isEmpty()) {
      throw new ReviewNotFoundException();
    }
    return review.get();
  }

  public Review getReviewByReservationId(@NotNull Long reservationId)
      throws ReviewNotFoundException {
    Optional<Review> review = reviewRepository.findByReservation_Id(reservationId);
    if (review.isEmpty()) {
      throw new ReviewNotFoundException();
    }
    return review.get();
  }

  public ReviewSearchResult getMagazineReviews(@NotNull Long magazineId, @NotNull Integer page) {
    Page<Review> result = reviewRepository
        .findAllByReservation_Magazine_Id(magazineId, PageRequest.of(page - 1, 10));

    return ReviewSearchResult.builder()
        .reviews(result.getContent())
        .records(result.getTotalElements())
        .pages(result.getTotalPages())
        .build();
  }

  public Integer getMagazineReviewsNumber(@NotNull Long magazineId) {
    return reviewRepository.countByReservation_Magazine_Id(magazineId);
  }

  public BigDecimal getMagazineAverageRating(@NotNull Long magazineId) {
    return reviewRepository.averageByReservation_Magazine_Id(magazineId);
  }
}
