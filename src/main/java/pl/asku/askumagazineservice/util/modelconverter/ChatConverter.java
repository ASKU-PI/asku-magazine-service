package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.chat.ChatDto;
import pl.asku.askumagazineservice.model.chat.Chat;

@Service
@AllArgsConstructor
public class ChatConverter {

  @Lazy
  UserConverter userConverter;

  public ChatDto toDto(Chat chat) {
    return ChatDto.builder()
        .userDto(userConverter.toDto(chat.getUser()))
        .unreadMessages(chat.getUnreadMessages())
        .build();
  }
}
