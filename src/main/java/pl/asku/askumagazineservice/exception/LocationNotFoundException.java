package pl.asku.askumagazineservice.exception;

public class LocationNotFoundException extends Exception {
  public LocationNotFoundException() {
    super("Location not found");
  }
}
