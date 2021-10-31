package pl.asku.askumagazineservice.dto.client.authservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResponseDto {
  public String password;
}
