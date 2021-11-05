package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.chat.ChatMessageDto;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.model.chat.MessageStatus;
import pl.asku.askumagazineservice.service.UserService;

@Service
@AllArgsConstructor
public class ChatMessageConverter {

  private UserService userService;
  @Lazy
  private UserConverter userConverter;

  public ChatMessage toChatMessage(ChatMessageRequestDto chatMessageRequestDto, User sender)
      throws UserNotFoundException {
    User receiver = userService.getUser(chatMessageRequestDto.getReceiverId());
    return ChatMessage.builder()
        .body(chatMessageRequestDto.getBody())
        .status(MessageStatus.RECEIVED)
        .receiver(receiver)
        .sender(sender)
        .build();
  }

  public ChatMessageDto toDto(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .id(chatMessage.getId())
        .createdDate(chatMessage.getCreatedDate())
        .body(chatMessage.getBody())
        .receiver(userConverter.toDto(chatMessage.getReceiver()))
        .sender(userConverter.toDto(chatMessage.getSender()))
        .build();
  }


}
