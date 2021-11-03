package pl.asku.askumagazineservice.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class ReportPolicy {

  public boolean addReport(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_USER"));
  }

  public boolean getReport(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_MODERATOR"));
  }

  public boolean updateReportState(Authentication authentication) {
    return getReport(authentication);
  }
}
