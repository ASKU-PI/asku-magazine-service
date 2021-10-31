package pl.asku.askumagazineservice.exception;

public class ReservationNotFoundException extends Exception {
  public ReservationNotFoundException() {
    super("Reservation not found");
  }
}
