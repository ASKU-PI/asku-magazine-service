package pl.asku.askumagazineservice.dto.client.imageservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MagazinePictureDto {
    private Long id;
    private List<PictureData> photos;
}
