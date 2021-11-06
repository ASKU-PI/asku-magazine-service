package pl.asku.askumagazineservice.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.service.ChatMessageService;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

public class GetChatMessagesServiceTests extends ChatMessageServiceTestBase {

  @Autowired
  public GetChatMessagesServiceTests(
      UserDataProvider userDataProvider,
      ChatMessageService chatMessageService,
      UserConverter userConverter) {
    super(userDataProvider, chatMessageService, userConverter);
  }

  @Test
  public void returnsListOfMessagesInCorrectOrder() throws UserNotFoundException {
    //given
    User user = userDataProvider.user("user@test.pl", "666666666");

    User otherUser = userDataProvider.user("other_user@test.pl", "777777777");

    for (int i = 0; i < 3; i++) {
      ChatMessageRequestDto chatMessageRequestDto = ChatMessageRequestDto.builder()
          .body("test message " + i)
          .receiverId(otherUser.getId())
          .build();

      chatMessageService.createMessage(chatMessageRequestDto, user);
    }

    ChatMessageRequestDto chatMessageRequestDto1 = ChatMessageRequestDto.builder()
        .body("test message")
        .receiverId(user.getId())
        .build();

    chatMessageService.createMessage(chatMessageRequestDto1, otherUser);

    //when
    List<ChatMessage> chatMessageList =
        chatMessageService.getChatMessages(user.getId(), otherUser.getId());

    //then
    assertEquals(chatMessageList.size(), 4);
  }
}
