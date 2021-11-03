package pl.asku.askumagazineservice.controller;

import java.util.Optional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.asku.askumagazineservice.dto.report.ReportDto;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.ReportNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.report.Report;
import pl.asku.askumagazineservice.security.policy.ReportPolicy;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReportService;
import pl.asku.askumagazineservice.service.UserService;
import pl.asku.askumagazineservice.util.modelconverter.ReportConverter;
import pl.asku.askumagazineservice.util.modelconverter.SearchResultConverter;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class ReportController {

  private final MagazineService magazineService;
  private final UserService userService;
  private final ReportPolicy reportPolicy;
  private final ReportService reportService;
  private final ReportConverter reportConverter;
  private final SearchResultConverter searchResultConverter;

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/report")
  public ResponseEntity<Object> addReport(
      @RequestBody @Valid ReportDto reportDto,
      @RequestParam @NotNull Long magazineId,
      Authentication authentication) {
    if (!reportPolicy.addReport(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You're not authorized to add reports");
    }

    try {
      User user = userService.getUser(authentication.getName());
      Magazine magazine = magazineService.getMagazine(magazineId);
      Report report = reportService.addReport(reportDto, user, magazine);
      return ResponseEntity.status(HttpStatus.CREATED).body(reportConverter.toDto(report));
    } catch (UserNotFoundException | MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }
  }

  @GetMapping("/report")
  public ResponseEntity<Object> getReport(
      @RequestParam Long id,
      Authentication authentication) {
    if (!reportPolicy.getReport(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "You're not authorized to get the report");
    }

    try {
      Report report = reportService.getReport(id);
      return ResponseEntity.status(HttpStatus.OK).body(reportConverter.toDto(report));
    } catch (ReportNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/reports/open")
  public ResponseEntity<Object> getOpenReports(
      @RequestParam(required = false) Optional<Integer> page,
      Authentication authentication) {
    if (!reportPolicy.getReport(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "You're not authorized to get report list");
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(searchResultConverter.toDto(
            reportService.getOpenReports(page.orElse(1))
        ));
  }

  @GetMapping("/reports/closed")
  public ResponseEntity<Object> getClosedReports(
      @RequestParam(required = false) Optional<Integer> page,
      Authentication authentication) {
    if (!reportPolicy.getReport(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "You're not authorized to get report list");
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(searchResultConverter.toDto(
            reportService.getClosedReports(page.orElse(1))
        ));
  }

  @PatchMapping("/report")
  public ResponseEntity<Object> updateReportState(
      @RequestParam Long id,
      @RequestParam Boolean closed,
      Authentication authentication) {
    if (!reportPolicy.getReport(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "You're not authorized to update report state");
    }

    try {
      Report report = reportService.getReport(id);
      if (closed) {
        report = reportService.closeReport(report);
      } else {
        report = reportService.reopenReport(report);
      }
      return ResponseEntity.status(HttpStatus.OK).body(reportConverter.toDto(report));
    } catch (ReportNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }
  }

}
