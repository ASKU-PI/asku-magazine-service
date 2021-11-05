package pl.asku.askumagazineservice.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.model.chat.ChatMessage;

@Component
public class ChatPolicy {

  public boolean sendMessage(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_USER"));
  }

  public boolean getMessage(Authentication authentication, ChatMessage chatMessage) {
    return getMessage(
        authentication, chatMessage.getSender().getId(), chatMessage.getReceiver().getId());
  }

  public boolean getMessage(Authentication authentication, String userOneId, String userTwoId) {
    boolean isModerator = authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority(
        "ROLE_MODERATOR"));

    boolean isParticipant = authentication != null
        && (authentication.getName().equals(userOneId)
        || authentication.getName().equals(userTwoId));

    return isModerator || isParticipant;
  }

  public boolean getChatsList(Authentication authentication) {
    return sendMessage(authentication);
  }
}
