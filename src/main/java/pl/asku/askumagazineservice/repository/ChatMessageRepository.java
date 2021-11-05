package pl.asku.askumagazineservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.model.chat.MessageStatus;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {

  Long countBySender_IdAndReceiver_IdAndStatus(
      String senderId, String receiverId, MessageStatus status);

  Long countByReceiver_IdAndStatus(String receiverId, MessageStatus status);

  @Query("SELECT m from ChatMessage m WHERE (m.sender.id = :senderId AND m.receiver.id = "
      + ":receiverId) OR (m.sender.id = :receiverId AND m.receiver.id = :senderId) ORDER BY m"
      + ".createdDate DESC")
  List<ChatMessage> findAllBySender_IdAndReceiver_Id(String senderId, String receiverId);

  @Query("SELECT DISTINCT u FROM User u WHERE u.id IN (SELECT u.id FROM ChatMessage m JOIN User u ON m"
      + ".sender.id = u.id WHERE m.receiver.id = :userId) OR u.id IN (SELECT u.id FROM "
      + "ChatMessage m JOIN User u ON m.receiver.id = u.id WHERE m.sender.id = :userId)")
  List<User> getChats(String userId);
}
