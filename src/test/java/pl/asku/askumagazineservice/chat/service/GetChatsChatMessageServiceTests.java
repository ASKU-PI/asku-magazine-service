package pl.asku.askumagazineservice.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.Chat;
import pl.asku.askumagazineservice.service.ChatMessageService;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

public class GetChatsChatMessageServiceTests extends ChatMessageServiceTestBase {

  @Autowired
  public GetChatsChatMessageServiceTests(
      UserDataProvider userDataProvider,
      ChatMessageService chatMessageService,
      UserConverter userConverter) {
    super(userDataProvider, chatMessageService, userConverter);
  }

  @Test
  public void returnsCorrectChatList() throws UserNotFoundException {
    //given
    User user = userDataProvider.user("user@test.pl", "666666666");

    User receiver1 = userDataProvider.user("receiver1@test.pl", "777777777");

    ChatMessageRequestDto chatMessageRequestDto = ChatMessageRequestDto.builder()
        .body("test message")
        .receiverId(receiver1.getId())
        .build();

    for (int i = 0; i < 3; i++) {
      chatMessageService.createMessage(chatMessageRequestDto, user);
    }

    User receiver2 = userDataProvider.user("receiver2@test.pl", "888888888");
    ChatMessageRequestDto chatMessageRequestDto1 = ChatMessageRequestDto.builder()
        .body("test message")
        .receiverId(receiver2.getId())
        .build();

    chatMessageService.createMessage(chatMessageRequestDto1, user);

    User sender = userDataProvider.user("sender@test.pl", "999999999");
    ChatMessageRequestDto chatMessageRequestDto2 = ChatMessageRequestDto.builder()
        .body("test message")
        .receiverId(user.getId())
        .build();

    chatMessageService.createMessage(chatMessageRequestDto2, sender);

    //when
    List<Chat> chats = chatMessageService.getChats(user.getId());

    //then
    assertEquals(chats.size(), 3);

    assertEquals((int) chats.stream().filter(
        chat -> chat.getUser() == receiver1 && chat.getUnreadMessages() == 0).count(), 1);

    assertEquals((int) chats.stream().filter(
        chat -> chat.getUser() == receiver2 && chat.getUnreadMessages() == 0).count(), 1);

    assertEquals((int) chats.stream().filter(
        chat -> chat.getUser() == sender && chat.getUnreadMessages() == 1).count(), 1);
  }
}
