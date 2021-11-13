package pl.asku.askumagazineservice.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.exception.ChatMessageNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.Chat;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.model.chat.MessageStatus;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;
import pl.asku.askumagazineservice.repository.ChatMessageRepository;
import pl.asku.askumagazineservice.util.modelconverter.ChatMessageConverter;

@Service
@Validated
@AllArgsConstructor
public class ChatMessageService {
  private ChatMessageRepository repository;
  private ChatMessageConverter chatMessageConverter;

  public ChatMessage createMessage(
      @Valid @NotNull ChatMessageRequestDto chatMessageRequestDto,
      @Valid @NotNull User sender)
      throws UserNotFoundException {
    ChatMessage chatMessage = chatMessageConverter.toChatMessage(chatMessageRequestDto, sender);
    return repository.save(chatMessage);
  }

  public ChatMessage createMessage(
      @Valid @NotNull Reservation reservation
  ) {
    ChatMessage chatMessage = chatMessageConverter.toChatMessage(reservation);
    return repository.save(chatMessage);
  }

  public ChatMessage createMessage(
      @Valid @NotNull Review review
      ) {
    ChatMessage chatMessage = chatMessageConverter.toChatMessage(review);
    return repository.save(chatMessage);
  }

  public Long countNewMessages(String receiverId) {
    return repository.countByReceiver_IdAndStatus(receiverId, MessageStatus.RECEIVED);
  }

  public long countNewMessages(String senderId, String recipientId) {
    return repository.countBySender_IdAndReceiver_IdAndStatus(
        senderId, recipientId, MessageStatus.RECEIVED);
  }

  public List<Chat> getChats(String userId) {
    return repository.getChats(userId)
        .stream().map(user -> new Chat(user, countNewMessages(user.getId(), userId))).collect(
            Collectors.toList());
  }

  public List<ChatMessage> getChatMessages(String senderId, String recipientId)
      throws UserNotFoundException {

    var messages = repository.findAllBySender_IdAndReceiver_Id(senderId, recipientId);

    updateStatuses(messages, MessageStatus.DELIVERED);

    return messages;
  }

  public ChatMessage getChatMessage(Long id) throws ChatMessageNotFoundException {
    return repository
        .findById(id)
        .map(chatMessage -> {
          chatMessage.setStatus(MessageStatus.DELIVERED);
          return repository.save(chatMessage);
        })
        .orElseThrow(ChatMessageNotFoundException::new);
  }

  private void updateStatuses(List<ChatMessage> messages, MessageStatus status) {
    for (ChatMessage message : messages) {
      message.setStatus(status);
      repository.save(message);
    }
  }
}
