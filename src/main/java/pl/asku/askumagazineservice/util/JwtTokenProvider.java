package pl.asku.askumagazineservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider implements InitializingBean {

   private final String secretKey;

   private Key key;

   public JwtTokenProvider(
      @Value("${jwt.secret}") String secretKey) {
      this.secretKey = secretKey;
   }

   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   public Authentication getAuthentication(String token) {
      Claims claims = Jwts
              .parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();

      Collection<? extends GrantedAuthority> authorities =
              Arrays.stream(claims.get("auth").toString().split(","))
                      .map(SimpleGrantedAuthority::new)
                      .collect(Collectors.toList());

      User principal = new User(claims.getSubject(), "", authorities);

      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   public boolean checkToken(String token) {
      try {
         Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
         return true;
      } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
         e.printStackTrace();
      }
      return false;
   }
}
