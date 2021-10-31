package pl.asku.askumagazineservice.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.user.UserDto;

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
