package pl.asku.askumagazineservice.dto.client.imageservice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MagazinePictureDto {
  private Long id;
  private List<PictureData> photos;
}
