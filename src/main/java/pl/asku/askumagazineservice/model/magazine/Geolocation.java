package pl.asku.askumagazineservice.model.magazine;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Geolocation {

    private BigDecimal longitude;
    private BigDecimal latitude;
}