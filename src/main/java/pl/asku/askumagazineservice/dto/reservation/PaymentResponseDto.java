package pl.asku.askumagazineservice.dto.reservation;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private String clientSecret;
}