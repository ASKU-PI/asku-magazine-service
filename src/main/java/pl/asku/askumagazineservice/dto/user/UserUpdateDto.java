package pl.asku.askumagazineservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
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
public class UserUpdateDto {
  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Pattern(regexp = "[0-9\\-+\\s()]+")
  @Size(min = 3, max = 15)
  private String phoneNumber;

  @Size(max = 100)
  private String address;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthDate;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private PictureData avatar;
}
