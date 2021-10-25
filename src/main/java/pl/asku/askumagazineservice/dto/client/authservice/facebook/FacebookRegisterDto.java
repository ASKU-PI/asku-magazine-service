package pl.asku.askumagazineservice.dto.client.authservice.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookRegisterDto {
    @NotBlank
    private String accessToken;
}
