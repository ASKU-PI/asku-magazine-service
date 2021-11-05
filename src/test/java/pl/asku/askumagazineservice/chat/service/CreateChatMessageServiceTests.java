package pl.asku.askumagazineservice.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.service.chat.ChatMessageService;

public class CreateChatMessageServiceTests extends ChatMessageServiceTestBase {

  @Autowired
  public CreateChatMessageServiceTests(
      UserDataProvider userDataProvider,
      ChatMessageService chatMessageService) {
    super(userDataProvider, chatMessageService);
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
}
