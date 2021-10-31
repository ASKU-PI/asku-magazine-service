package pl.asku.askumagazineservice.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.model.magazine.Magazine;

@Component
public class MagazinePolicy {

  public boolean addMagazine(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_USER"));
  }

  public boolean updateMagazine(Authentication authentication, Magazine magazine) {
    boolean isOwner = authentication != null
        && authentication.getName().equals(magazine.getOwnerId());

    boolean isModerator = authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_MODERATOR"));

    return isOwner || isModerator;
  }

}
