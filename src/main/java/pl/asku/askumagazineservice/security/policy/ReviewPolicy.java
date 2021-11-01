package pl.asku.askumagazineservice.security.policy;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.model.Review;

@Component
@AllArgsConstructor
public class ReviewPolicy {

  public boolean addReview(Authentication authentication, Review review) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_USER"
    )) && authentication.getName().equals(review.getReservation().getUser().getId());
  }
}
