package pl.asku.askumagazineservice.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.asku.askumagazineservice.dto.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.exception.ReviewNotFoundException;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.security.policy.ReviewPolicy;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final ReviewConverter reviewConverter;
  private final ReviewPolicy reviewPolicy;

  private final ReservationService reservationService;

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/review")
  public ResponseEntity<Object> addReview(
      @RequestBody @Valid ReviewDto reviewDto,
      @RequestParam Long reservationId,
      Authentication authentication) {
    try {
      Reservation reservation = reservationService.getReservation(reservationId);

      if (!reviewPolicy.addReview(authentication, reservation)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cannot add the review.");
      }

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(reviewService.addReview(reviewDto, reservation));
    } catch (ReservationNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    } catch (ReviewAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @GetMapping("/review")
  public ResponseEntity<Object> getReview(@RequestParam Long id) {
    try {
      Review review = reviewService.getReview(id);
      return ResponseEntity.status(HttpStatus.OK).body(reviewConverter.toDto(review));
    } catch (ReviewNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
