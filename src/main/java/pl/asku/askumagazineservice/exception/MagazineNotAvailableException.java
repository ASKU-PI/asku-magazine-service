package pl.asku.askumagazineservice.exception;

public class MagazineNotAvailableException extends Exception {
    public MagazineNotAvailableException() {super("Magazine is not available to rent");}
}
