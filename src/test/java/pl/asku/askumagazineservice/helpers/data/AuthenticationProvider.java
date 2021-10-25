package pl.asku.askumagazineservice.helpers.data;

import lombok.AllArgsConstructor;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@Service
@AllArgsConstructor
public class AuthenticationProvider {

    private final UserDataProvider userDataProvider;

    public Authentication userAuthentication() {
        UserDto userDto = userDataProvider.getUser("user@test.pl");

        var auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDto.getFirstName() + " " + userDto.getLastName());
        when(auth.getName()).thenReturn(userDto.getId());
        when(auth.getAuthorities()).thenReturn((Collection) Stream.of("ROLE_USER").map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }
}
