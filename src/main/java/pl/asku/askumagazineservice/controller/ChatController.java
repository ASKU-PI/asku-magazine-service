package pl.asku.askumagazineservice.controller;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.asku.askumagazineservice.dto.chat.ChatDto;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.model.chat.ChatNotification;
import pl.asku.askumagazineservice.security.policy.ChatPolicy;
import pl.asku.askumagazineservice.service.UserService;
import pl.asku.askumagazineservice.service.chat.ChatMessageService;
import pl.asku.askumagazineservice.util.modelconverter.ChatConverter;
import pl.asku.askumagazineservice.util.modelconverter.ChatMessageConverter;

@Controller
@AllArgsConstructor
public class ChatController {

  private SimpMessagingTemplate messagingTemplate;
  private ChatMessageService chatMessageService;
  private UserService userService;
  private ChatPolicy chatPolicy;
  private ChatMessageConverter chatMessageConverter;
  private ChatConverter chatConverter;

  @PostMapping("/message")
  public ResponseEntity<Object> addMessage(
      @RequestBody ChatMessageRequestDto chatMessageRequestDto, Authentication authentication) {
    if (!chatPolicy.sendMessage(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You're not authorized to chat.");
    }

    try {
      User sender = userService.getUser(authentication.getName());
      ChatMessage chatMessage = chatMessageService.createMessage(chatMessageRequestDto, sender);

      messagingTemplate.convertAndSendToUser(
          chatMessage.getReceiver().getId(), "/queue/messages",
          new ChatNotification(
              chatMessage.getId(),
              chatMessage.getSender().getId(),
              chatMessage.getSender().getFirstName() + " "
                  + chatMessage.getSender().getLastName()));

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(chatMessageConverter.toDto(chatMessage));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }
  }

  @GetMapping("/messages/count-new")
  public ResponseEntity<Object> getNewMessagesCount(Authentication authentication) {
    if (!chatPolicy.getChatsList(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cannot get chat");
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(chatMessageService.countNewMessages(authentication.getName()));
  }

  @GetMapping("/chats")
  public ResponseEntity<Object> getChats(Authentication authentication) {
    if (!chatPolicy.getChatsList(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cannot get chat");
    }

    List<ChatDto> chats = chatMessageService.getChats(authentication.getName())
        .stream().map(chat -> chatConverter.toDto(chat)).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(chats);
  }

  @GetMapping("/messages/{userId}")
  public ResponseEntity<?> getMessagesFromUser(
      @PathVariable String userId,
      Authentication authentication) {
    if (!chatPolicy.getMessage(authentication, authentication.getName(), userId)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cannot get chat");
    }

    try {
      return ResponseEntity
          .ok(chatMessageService.findChatMessages(authentication.getName(), userId));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }
  }
}
