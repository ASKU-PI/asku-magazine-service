package pl.asku.askumagazineservice.dto.chat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDto {

  @NotNull
  @NotBlank
  private String receiverId;

  @NotNull
  @NotBlank
  @Size(max = 500)
  private String body;
}
