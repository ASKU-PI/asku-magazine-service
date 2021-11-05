package pl.asku.askumagazineservice.exception;

public class ChatMessageNotFoundException extends Exception {
  public ChatMessageNotFoundException() {
    super("Chat message not found!");
  }
}
