package pl.asku.askumagazineservice.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.service.chat.ChatMessageService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ChatMessageServiceTestBase {

  protected final UserDataProvider userDataProvider;
  protected final ChatMessageService chatMessageService;

  @Autowired
  public ChatMessageServiceTestBase(UserDataProvider userDataProvider, ChatMessageService chatMessageService) {
    this.userDataProvider = userDataProvider;
    this.chatMessageService = chatMessageService;
  }
}
