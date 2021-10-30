package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.exception.ReviewNotFoundException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.repository.ReviewRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review addReview(@Valid Review review) throws ReviewAlreadyExistsException {
        try {
            getReviewByReservationId(review.getReservation().getId());
            throw new ReviewAlreadyExistsException();
        } catch (ReviewNotFoundException e) {
            return reviewRepository.save(review);
        }
    }

    public Review getReview(@NotNull Long id) throws ReviewNotFoundException {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty()) throw new ReviewNotFoundException();
        return review.get();
    }

    public Review getReviewByReservationId(@NotNull Long reservationId) throws ReviewNotFoundException {
        Optional<Review> review = reviewRepository.findByReservation_Id(reservationId);
        if (review.isEmpty()) throw new ReviewNotFoundException();
        return review.get();
    }
}
