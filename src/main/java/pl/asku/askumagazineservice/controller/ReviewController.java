package pl.asku.askumagazineservice.controller;

import java.util.Optional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.asku.askumagazineservice.dto.review.ReviewDto;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.exception.ReviewNotFoundException;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.model.chat.ChatNotification;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;
import pl.asku.askumagazineservice.model.review.ReviewSearchResult;
import pl.asku.askumagazineservice.security.policy.ReviewPolicy;
import pl.asku.askumagazineservice.service.ChatMessageService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;
import pl.asku.askumagazineservice.util.modelconverter.ReviewConverter;
import pl.asku.askumagazineservice.util.modelconverter.SearchResultConverter;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final ReviewConverter reviewConverter;
  private final ReviewPolicy reviewPolicy;
  private final SearchResultConverter searchResultConverter;

  private final ReservationService reservationService;

  private final SimpMessagingTemplate messagingTemplate;
  private final ChatMessageService chatMessageService;

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

      Review review = reviewService.addReview(reviewDto, reservation);

      ChatMessage chatMessage = chatMessageService.createMessage(review);

      messagingTemplate.convertAndSendToUser(
          chatMessage.getReceiver().getId(), "/queue/messages",
          new ChatNotification(
              chatMessage.getId(),
              chatMessage.getSender().getId(),
              chatMessage.getSender().getFirstName() + " "
                  + chatMessage.getSender().getLastName()));

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(review);
    } catch (ReservationNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    } catch (ReviewAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @PatchMapping("/review")
  public ResponseEntity<Object> updateReview(
      @RequestParam Long reviewId,
      @RequestBody @Valid ReviewDto reviewDto,
      Authentication authentication
  ) {
    try {
      Review review = reviewService.getReview(reviewId);

      if (!reviewPolicy.updateReview(authentication, review)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to update this review");
      }

      return ResponseEntity.status(HttpStatus.OK).body(
          reviewConverter.toDto(reviewService.updateReview(
              review, reviewDto)));
    } catch (ReviewNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @DeleteMapping("/review")
  public ResponseEntity<Object> deleteReview(
      @RequestParam Long reviewId,
      Authentication authentication
  ) {
    try {
      Review review = reviewService.getReview(reviewId);

      if (!reviewPolicy.deleteReview(authentication, review)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to delete this review");
      }

      reviewService.deleteReview(review);

      return ResponseEntity.status(HttpStatus.OK).body("Review deleted");
    } catch (ReviewNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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

  @GetMapping("/reservation-review")
  public ResponseEntity<Object> getReservationReview(@RequestParam Long reservationId) {
    try {
      Review review = reviewService.getReviewByReservationId(reservationId);
      return ResponseEntity.status(HttpStatus.OK).body(reviewConverter.toDto(review));
    } catch (ReviewNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/magazine-reviews")
  public ResponseEntity<Object> getMagazineReviews(
      @RequestParam Long magazineId,
      @RequestParam(required = false) Optional<Integer> page
  ) {
    ReviewSearchResult reviewSearchResult =
        reviewService.getMagazineReviews(magazineId, page.orElse(1));
    return ResponseEntity.status(HttpStatus.OK)
        .body(searchResultConverter.toDto(reviewSearchResult));
  }
}
