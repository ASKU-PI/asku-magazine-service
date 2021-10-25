package pl.asku.askumagazineservice.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class UserPolicy {

    public boolean getUser(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
                "ROLE_USER"
        ));
    }
}
