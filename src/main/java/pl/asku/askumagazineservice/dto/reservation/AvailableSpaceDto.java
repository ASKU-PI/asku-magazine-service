package pl.asku.askumagazineservice.dto.reservation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSpaceDto {
    private Long magazineId;
    private BigDecimal availableArea;
}
