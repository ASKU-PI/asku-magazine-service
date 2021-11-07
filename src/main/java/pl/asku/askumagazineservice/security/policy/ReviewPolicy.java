package pl.asku.askumagazineservice.security.policy;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;

@Component
@AllArgsConstructor
public class ReviewPolicy {

  public boolean addReview(Authentication authentication, Reservation reservation) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_USER"
    )) && authentication.getName().equals(reservation.getUser().getId());
  }

  public boolean updateReview(Authentication authentication, Review review) {
    boolean isOwner = authentication != null
        && authentication.getName().equals(review.getReservation().getUser().getId());

    boolean isModerator = authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
            "ROLE_MODERATOR"));

    return isOwner || isModerator;
  }

  public boolean deleteReview(Authentication authentication, Review review) {
    return updateReview(authentication, review);
  }
}
