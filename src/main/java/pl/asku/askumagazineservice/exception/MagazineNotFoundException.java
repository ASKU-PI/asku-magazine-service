package pl.asku.askumagazineservice.exception;

public class MagazineNotFoundException extends Exception {
  public MagazineNotFoundException() {
    super("Magazine not found");
  }
}
