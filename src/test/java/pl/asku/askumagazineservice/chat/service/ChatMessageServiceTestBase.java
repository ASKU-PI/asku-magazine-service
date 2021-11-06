package pl.asku.askumagazineservice.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.service.ChatMessageService;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ChatMessageServiceTestBase {

  protected final UserDataProvider userDataProvider;
  protected final ChatMessageService chatMessageService;
  protected final UserConverter userConverter;

  @Autowired
  public ChatMessageServiceTestBase(UserDataProvider userDataProvider,
                                    ChatMessageService chatMessageService,
                                    UserConverter userConverter) {
    this.userDataProvider = userDataProvider;
    this.chatMessageService = chatMessageService;
    this.userConverter = userConverter;
  }
}
