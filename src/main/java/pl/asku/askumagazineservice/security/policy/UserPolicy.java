package pl.asku.askumagazineservice.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.model.User;

@Component
public class UserPolicy {

    public boolean getUser(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
                "ROLE_USER"
        ));
    }

    public boolean getUserPersonal(Authentication authentication, User user) {
        boolean isOwner = authentication != null && authentication.getName().equals(user.getId());

        boolean isModerator = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
                "ROLE_MODERATOR"
        ));

        return isOwner || isModerator;
    }
}
