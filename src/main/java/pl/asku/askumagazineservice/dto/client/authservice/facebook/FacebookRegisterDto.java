package pl.asku.askumagazineservice.dto.client.authservice.facebook;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookRegisterDto {
  @NotBlank
  private String accessToken;
}
