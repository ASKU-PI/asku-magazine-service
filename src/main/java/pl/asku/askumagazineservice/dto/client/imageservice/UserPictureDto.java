package pl.asku.askumagazineservice.dto.client.imageservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPictureDto {
  private PictureData photo;
}
