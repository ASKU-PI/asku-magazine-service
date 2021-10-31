package pl.asku.askumagazineservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @NotNull
  @NotBlank
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotNull
  @NotBlank
  private String password;
}
