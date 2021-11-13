package pl.asku.askumagazineservice.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.asku.askumagazineservice.dto.chat.ChatMessageRequestDto;
import pl.asku.askumagazineservice.dto.reservation.AvailableSpaceDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.chat.ChatMessage;
import pl.asku.askumagazineservice.model.chat.ChatNotification;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.security.policy.ReservationPolicy;
import pl.asku.askumagazineservice.service.ChatMessageService;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.UserService;
import pl.asku.askumagazineservice.util.modelconverter.ReservationConverter;

@RestController
@Validated
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;
  private final MagazineService magazineService;
  private final ReservationPolicy reservationPolicy;
  private final ReservationConverter reservationConverter;
  private final UserService userService;
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatMessageService chatMessageService;

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/add")
  public ResponseEntity<Object> addReservation(
      @RequestBody @Valid ReservationDto reservationDto,
      Authentication authentication) {
    if (!reservationPolicy.addReservation(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reservationDto);
    }

    String username = authentication.getName();

    try {
      User user = userService.getUser(username);
      Reservation reservation = reservationService.addReservation(reservationDto, user);
      reservationDto.setId(reservation.getId());

      ChatMessage chatMessage = chatMessageService.createMessage(reservation);

      messagingTemplate.convertAndSendToUser(
          chatMessage.getReceiver().getId(), "/queue/messages",
          new ChatNotification(
              chatMessage.getId(),
              chatMessage.getSender().getId(),
              chatMessage.getSender().getFirstName() + " "
                  + chatMessage.getSender().getLastName()));

      return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
    } catch (MagazineNotAvailableException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (MagazineNotFoundException | UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }
  }

  @GetMapping("/reservation/{id}")
  public ResponseEntity<Object> getReservation(
      @PathVariable Long id,
      Authentication authentication
  ) {
    try {
      Reservation reservation = reservationService.getReservation(id);

      if (!reservationPolicy.getReservation(authentication, reservation)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You're not authorized to get this reservation");
      }

      return ResponseEntity.status(HttpStatus.OK).body(reservationConverter.toDto(reservation));
    } catch (ReservationNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/daily-reservations/{id}")
  public ResponseEntity<Object> getDailyReservations(
      @PathVariable Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
      Authentication authentication
  ) {
    try {
      if (!reservationPolicy.getReservations(authentication, id)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You're not authorized to get reservations of this space");
      }
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            reservationService.getDailyReservations(id, day)
                .stream()
                .map(reservationConverter::toDto)
                .collect(Collectors.toList()
                ));
  }

  @GetMapping("/daily-states/{id}")
  public ResponseEntity<Object> getDailyStates(
      @PathVariable Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
  ) {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(reservationService.getDailyStates(id, fromDate, toDate));
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/daily-availability/{id}")
  public ResponseEntity<Object> getDailyAvailability(
      @PathVariable Long id,
      @RequestParam BigDecimal area,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
  ) {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(reservationService.getDailyAvailability(id, area, fromDate, toDate));
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/availability/{id}")
  public ResponseEntity<Object> magazineAvailable(
      @PathVariable @NotNull Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
      @RequestParam @Min(0) BigDecimal minArea
  ) {
    try {
      Magazine magazine = magazineService.getMagazine(id);
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(reservationService.checkIfMagazineAvailable(magazine, start, end, minArea));
    } catch (ValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

  }

  @GetMapping("/available-area/{id}")
  public ResponseEntity<Object> availableArea(
      @PathVariable @NotNull Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end
  ) {
    try {
      Magazine magazine = magazineService.getMagazine(id);
      BigDecimal availableArea = reservationService.getAvailableArea(magazine, start, end);
      return ResponseEntity.status(HttpStatus.OK)
          .body(new AvailableSpaceDto(magazine.getId(), availableArea));
    } catch (ValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/total-price/{id}")
  public ResponseEntity<Object> totalPrice(
      @PathVariable @NotNull Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
      @RequestParam @Min(0) BigDecimal area
  ) {
    try {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(reservationService.getTotalPrice(ReservationDto.builder()
              .magazineId(id)
              .startDate(start)
              .endDate(end)
              .areaInMeters(area)
              .build()
          ));
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }
  }
}
