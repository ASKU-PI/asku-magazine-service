package pl.asku.askumagazineservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.asku.askumagazineservice.dto.client.imageservice.PictureData;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String id;

  @NotNull
  @NotBlank
  @Size(max = 50)
  private String firstName;

  @NotNull
  @NotBlank
  @Size(max = 50)
  private String lastName;

  @Pattern(regexp = "[0-9\\-+\\s()]+")
  @Size(min = 3, max = 15)
  private String phoneNumber;

  @Size(max = 100)
  private String address;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthDate;

  private String email;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private PictureData avatar;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long ownedSpacesCount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long reservationsCount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long reviewsCount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BigDecimal averageRating;
}
