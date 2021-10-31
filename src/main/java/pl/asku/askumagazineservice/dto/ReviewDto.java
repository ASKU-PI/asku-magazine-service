package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate createdDate;

  @NotNull
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long reservationId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private ReservationDto reservationDto;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer rating;

  @Size(max = 500)
  private String body;
}
