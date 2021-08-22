package pl.asku.askumagazineservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

   private SecurityUtil() {
   }

   public static Optional<String> getCurrentUsername() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
         return Optional.empty();
      }

      String username = null;
      if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
         username = springSecurityUser.getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
         username = (String) authentication.getPrincipal();
      }

      return Optional.ofNullable(username);
   }
}
