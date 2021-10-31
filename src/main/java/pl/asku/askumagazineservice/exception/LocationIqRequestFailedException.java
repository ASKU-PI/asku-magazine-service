package pl.asku.askumagazineservice.exception;

public class LocationIqRequestFailedException extends Exception {
  public LocationIqRequestFailedException() {
    super("Location IQ request has failed");
  }
}
