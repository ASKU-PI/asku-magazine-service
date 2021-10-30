package pl.asku.askumagazineservice.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.asku.askumagazineservice.dto.UserDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.model.magazine.Magazine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto user;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal areaInMeters;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long magazineId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MagazineDto magazine;
}
