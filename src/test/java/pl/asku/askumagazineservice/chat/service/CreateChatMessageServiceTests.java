package pl.asku.askumagazineservice.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.service.ChatMessageService;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

public class CreateChatMessageServiceTests extends ChatMessageServiceTestBase {

  @Autowired
  public CreateChatMessageServiceTests(
      UserDataProvider userDataProvider,
      ChatMessageService chatMessageService,
      UserConverter userConverter) {
    super(userDataProvider, chatMessageService, userConverter);
  }

  @Test
  public void addsMessage() throws UserNotFoundException {
    //given
    User sender = userDataProvider.user("sender@test.pl", "666666666");
    User receiver = userDataProvider.user("receiver@test.pl", "777777777");

    ChatMessageRequestDto chatMessageRequestDto = ChatMessageRequestDto.builder()
        .body("test message")
        .receiverId(receiver.getId())
        .build();

    //when
    ChatMessage chatMessage = chatMessageService.createMessage(chatMessageRequestDto, sender);

    //then
    assertEquals(chatMessage.getSender(), sender);
    assertEquals(chatMessage.getReceiver(), receiver);
    assertEquals(chatMessage.getBody(), chatMessageRequestDto.getBody());
  }

  @Test
  public void failsWhenEmptyBody() {
    //given
    User sender = userDataProvider.user("sender@test.pl", "666666666");
    User receiver = userDataProvider.user("receiver@test.pl", "777777777");

    ChatMessageRequestDto chatMessageRequestDto = ChatMessageRequestDto.builder()
        .body("")
        .receiverId(receiver.getId())
        .build();

    //when then
    assertThrows(ConstraintViolationException.class,
        () -> chatMessageService.createMessage(chatMessageRequestDto, sender));
  }

  @Test
  public void failsWhenNullBody() {
    //given
    User sender = userDataProvider.user("sender@test.pl", "666666666");
    User receiver = userDataProvider.user("receiver@test.pl", "777777777");

    ChatMessageRequestDto chatMessageRequestDto = ChatMessageRequestDto.builder()
        .body(null)
        .receiverId(receiver.getId())
        .build();

    //when then
    assertThrows(ConstraintViolationException.class,
        () -> chatMessageService.createMessage(chatMessageRequestDto, sender));
  }

  @Test
  public void failsWhenNonExistentReceiver() {
    //given
    User sender = userDataProvider.user("sender@test.pl", "666666666");

    ChatMessageRequestDto chatMessageRequestDto = ChatMessageRequestDto.builder()
        .body("test body")
        .receiverId("nonexistent@test.pl")
        .build();

    //when then
    assertThrows(UserNotFoundException.class,
        () -> chatMessageService.createMessage(chatMessageRequestDto, sender));
  }
}
