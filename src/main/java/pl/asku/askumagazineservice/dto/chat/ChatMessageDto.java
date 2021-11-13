package pl.asku.askumagazineservice.dto.chat;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.dto.user.UserDto;
import pl.asku.askumagazineservice.model.chat.MessageType;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
  private Long id;
  private String body;
  private Date createdDate;
  private UserDto sender;
  private UserDto receiver;
  private MessageType messageType;
}
