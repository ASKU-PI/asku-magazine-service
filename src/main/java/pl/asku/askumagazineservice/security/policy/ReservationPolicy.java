package pl.asku.askumagazineservice.security.policy;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;

@Component
@AllArgsConstructor
public class ReservationPolicy {

  private final MagazineService magazineService;

  public boolean addReservation(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_USER"));
  }

  public boolean getReservation(Authentication authentication, Reservation reservation) {
    boolean atLeastModerator =
        authentication != null
            && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
            "ROLE_MODERATOR"
        ));

    boolean isMagazineOwner =
        authentication != null
            && reservation.getMagazine().getOwner().getId().equals(authentication.getName());

    boolean isReservationOwner =
        authentication != null && reservation.getUser().getId().equals(authentication.getName());

    return atLeastModerator || isMagazineOwner || isReservationOwner;
  }

  public boolean getReservations(Authentication authentication, Long spaceId)
      throws MagazineNotFoundException {
    boolean atLeastModerator =
        authentication != null
            && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
            "ROLE_MODERATOR"
        ));

    boolean isMagazineOwner =
        authentication != null && magazineService.getMagazine(spaceId).getOwner().getId()
            .equals(authentication.getName());

    return atLeastModerator || isMagazineOwner;
  }
}
