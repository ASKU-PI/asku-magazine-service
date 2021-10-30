package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.security.policy.ReviewPolicy;
import pl.asku.askumagazineservice.service.ReviewService;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewConverter reviewConverter;
    private final ReviewPolicy reviewPolicy;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/review")
    public ResponseEntity<Object> addReview(
            @RequestBody @Valid ReviewDto reviewDto,
            Authentication authentication) {
        Review review;

        try {
            review = reviewConverter.toReview(reviewDto);
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        if (!reviewPolicy.addReview(authentication, review)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cannot add the review.");
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(review));
        } catch (ReviewAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
