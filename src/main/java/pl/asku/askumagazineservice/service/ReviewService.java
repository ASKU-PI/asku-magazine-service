package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.exception.ReviewNotFoundException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.repository.ReviewRepository;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;

    public Review addReview(@Valid Review review) {
        return reviewRepository.save(review);
    }

    public Review getReview(@NotNull Long id) throws ReviewNotFoundException {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty()) throw new ReviewNotFoundException();
        return review.get();
    }
}
