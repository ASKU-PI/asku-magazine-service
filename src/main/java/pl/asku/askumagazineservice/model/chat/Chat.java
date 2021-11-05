package pl.asku.askumagazineservice.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.asku.askumagazineservice.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
  private User user;
  private Long unreadMessages;
}
