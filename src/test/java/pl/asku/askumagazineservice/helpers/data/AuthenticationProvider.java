package pl.asku.askumagazineservice.helpers.data;

import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@Service
public class AuthenticationProvider {

    public Authentication userAuthentication() {
        var auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("testUser");
        when(auth.getName()).thenReturn("user@test.pl");
        when(auth.getAuthorities()).thenReturn((Collection) Stream.of("ROLE_USER").map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }
}
