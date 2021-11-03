package pl.asku.askumagazineservice.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class ReportDto {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdDate;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private UserDto reporter;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private MagazineDto magazine;

  @NotNull
  @Size(max = 500)
  private String body;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Boolean closed;
}
