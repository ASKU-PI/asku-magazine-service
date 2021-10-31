package pl.asku.askumagazineservice.helpers.data;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.model.User;

@Service
@AllArgsConstructor
public class AuthenticationProvider {

  private final UserDataProvider userDataProvider;

  public Authentication userAuthentication(User user) {
    return generateAuthentication(user, List.of("ROLE_USER"));
  }

  public Authentication moderatorAuthentication(User user) {
    return generateAuthentication(user, List.of("ROLE_USER", "ROLE_MODERATOR"));
  }

  public Authentication adminAuthentication(User user) {
    return generateAuthentication(user, List.of("ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN"));
  }

  private Authentication generateAuthentication(User user, List<String> roles) {
    var auth = Mockito.mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(user.getId());
    when(auth.getName()).thenReturn(user.getId());
    when(auth.getAuthorities()).thenReturn(
        (Collection) roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
    when(auth.isAuthenticated()).thenReturn(true);
    return auth;
  }
}
