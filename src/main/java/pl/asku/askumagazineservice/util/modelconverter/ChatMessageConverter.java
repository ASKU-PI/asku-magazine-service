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
import pl.asku.askumagazineservice.model.chat.MessageType;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.review.Review;
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
        .type(MessageType.MESSAGE)
        .build();
  }

  public ChatMessage toChatMessage(Reservation reservation) {
    User receiver = reservation.getMagazine().getOwner();
    User sender = reservation.getUser();
    String body = sender.getFirstName()
                + " has made a reservation to "
                + reservation.getMagazine().getTitle()
                + " for "
                + reservation.getAreaInMeters().toString()
                + " square meters in days "
                + reservation.getStartDate().toString()
                + " to "
                + reservation.getEndDate().toString()
                + ". Say hi and share some details!";

    return ChatMessage.builder()
        .body(body)
        .status(MessageStatus.RECEIVED)
        .receiver(receiver)
        .sender(sender)
        .type(MessageType.RESERVATION)
        .build();
  }

  public ChatMessage toChatMessage(Review review) {
    User receiver = review.getReservation().getMagazine().getOwner();
    User sender = review.getReservation().getUser();
    String body = sender.getFirstName()
        + " has gave a "
        + review.getRating()
        + " rating to the reservation to "
        + review.getReservation().getMagazine().getTitle()
        + " for "
        + review.getReservation().getAreaInMeters().toString()
        + " square meters in days "
        + review.getReservation().getStartDate().toString()
        + " to "
        + review.getReservation().getEndDate().toString()
        + ". Say hi and share some details!";

    return ChatMessage.builder()
        .body(body)
        .status(MessageStatus.RECEIVED)
        .receiver(receiver)
        .sender(sender)
        .type(MessageType.RATING)
        .build();
  }

  public ChatMessageDto toDto(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .id(chatMessage.getId())
        .createdDate(chatMessage.getCreatedDate())
        .body(chatMessage.getBody())
        .receiver(userConverter.toDto(chatMessage.getReceiver()))
        .sender(userConverter.toDto(chatMessage.getSender()))
        .messageType(chatMessage.getType())
        .build();
  }


}
