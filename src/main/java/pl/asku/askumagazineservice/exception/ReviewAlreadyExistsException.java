package pl.asku.askumagazineservice.exception;

public class ReviewAlreadyExistsException extends Exception {
  public ReviewAlreadyExistsException() {
    super("Review for this reservation already exists!");
  }
}
