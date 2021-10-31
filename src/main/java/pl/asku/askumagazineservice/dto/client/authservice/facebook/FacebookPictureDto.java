package pl.asku.askumagazineservice.dto.client.authservice.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacebookPictureDto {
  private PictureData data;
}
