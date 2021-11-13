package pl.asku.askumagazineservice.dto.user;

import java.time.LocalDate;
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

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
  @NotNull
  @NotBlank
  @Size(max = 50)
  private String firstName;

  @NotNull
  @NotBlank
  @Size(max = 50)
  private String lastName;

  @NotNull
  @Pattern(regexp = "[0-9\\-+\\s()]+")
  @Size(min = 3, max = 30)
  private String phoneNumber;

  @NotNull
  @NotBlank
  @Size(max = 100)
  private String address;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthDate;

  @NotNull
  @NotBlank
  private String email;

  @NotNull
  @NotBlank
  private String password;
}
