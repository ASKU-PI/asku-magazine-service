package pl.asku.askumagazineservice.exception;

public class ReportNotFoundException extends Exception {
  public ReportNotFoundException() {
    super("Report not found");
  }
}
