package pl.asku.askumagazineservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.dto.reservation.DailyStateDto;
import pl.asku.askumagazineservice.dto.reservation.DailyStatePercentageDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.AvailabilityState;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.model.reservation.TotalPrice;
import pl.asku.askumagazineservice.repository.ReservationRepository;
import pl.asku.askumagazineservice.util.validator.ReservationValidator;

@Service
@Validated
@AllArgsConstructor
public class ReservationService {

  private final ReservationValidator reservationValidator;
  private final MagazineService magazineService;
  private final ReservationRepository reservationRepository;

  @Transactional
  public Reservation addReservation(@NotNull @Valid ReservationDto reservationDto,
                                    @NotNull @Valid User user)
      throws MagazineNotAvailableException,
      MagazineNotFoundException {
    Magazine magazine = magazineService.getMagazine(reservationDto.getMagazineId());
    return addReservation(magazine, reservationDto, user);
  }

  @Transactional
  public Reservation addReservation(@NotNull @Valid Magazine magazine,
                                    @NotNull @Valid ReservationDto reservationDto,
                                    @NotNull @Valid User user)
      throws MagazineNotAvailableException {
    reservationValidator.validate(reservationDto);

    if (reservationDto.getStartDate().compareTo(reservationDto.getEndDate()) > 0
        || !checkIfMagazineAvailable(
        magazine,
        reservationDto.getStartDate(),
        reservationDto.getEndDate(),
        reservationDto.getAreaInMeters()
    )) {
      throw new MagazineNotAvailableException();
    }
    Reservation reservation = Reservation.builder()
        .user(user)
        .startDate(reservationDto.getStartDate())
        .endDate(reservationDto.getEndDate())
        .areaInMeters(reservationDto.getAreaInMeters())
        .magazine(magazine)
        .build();
    return reservationRepository.save(reservation);
  }

  public Reservation getReservation(@NotNull Long id) throws ReservationNotFoundException {
    Optional<Reservation> reservation = reservationRepository.findById(id);
    if (reservation.isEmpty()) {
      throw new ReservationNotFoundException();
    }
    return reservation.get();
  }

  public List<Reservation> getDailyReservations(Long id, LocalDate day) {
    return reservationRepository
        .findByMagazine_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(id, day, day);
  }

  public List<DailyStatePercentageDto> getDailyStates(Long id, LocalDate fromDate, LocalDate toDate)
      throws MagazineNotFoundException {
    List<Reservation> reservations =
        reservationRepository.findActiveReservations(id,
            fromDate, toDate);
    System.out.println(reservations);
    Magazine magazine = magazineService.getMagazine(id);
    List<DailyStatePercentageDto> result = new ArrayList<>();
    for (LocalDate date = fromDate; date.compareTo(toDate) <= 0; date = date.plusDays(1)) {
      LocalDate currentDate = date;
      if (magazine.getStartDate().compareTo(date) > 0
          || magazine.getEndDate().compareTo(date) < 0) {
        result.add(new DailyStatePercentageDto(id, date, AvailabilityState.UNAVAILABLE, null));
        continue;
      }
      List<Reservation> dailyReservations =
          reservations.stream().filter(
                  reservation -> reservation.getStartDate().compareTo(currentDate) <= 0
                      && reservation.getEndDate().compareTo(currentDate) >= 0)
              .collect(Collectors.toList());
      if (dailyReservations.isEmpty()) {
        result.add(new DailyStatePercentageDto(id, date, AvailabilityState.EMPTY, 0));
        continue;
      }
      BigDecimal takenArea = dailyReservations
          .stream()
          .map(Reservation::getAreaInMeters)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      if (takenArea.compareTo(magazine.getAreaInMeters()) == 0) {
        result.add(new DailyStatePercentageDto(id, date, AvailabilityState.FULL, 100));
        continue;
      }
      result.add(new DailyStatePercentageDto(id, date, AvailabilityState.SOME,
          takenArea.divide(magazine.getAreaInMeters(), RoundingMode.HALF_EVEN)
              .multiply(BigDecimal.valueOf(100)).intValue()));
    }
    return result;
  }

  public List<DailyStateDto> getDailyAvailability(
      Long id, BigDecimal area, LocalDate fromDate, LocalDate toDate)
      throws MagazineNotFoundException {
    List<Reservation> reservations =
        reservationRepository.findActiveReservations(id,
            fromDate, toDate);
    System.out.println(reservations);
    Magazine magazine = magazineService.getMagazine(id);
    List<DailyStateDto> result = new ArrayList<>();
    for (LocalDate date = fromDate; date.compareTo(toDate) <= 0; date = date.plusDays(1)) {
      LocalDate currentDate = date;
      if (magazine.getStartDate().compareTo(date) > 0
          || magazine.getEndDate().compareTo(date) < 0) {
        result.add(new DailyStateDto(id, date, AvailabilityState.UNAVAILABLE));
        continue;
      }
      List<Reservation> dailyReservations =
          reservations.stream().filter(
                  reservation -> reservation.getStartDate().compareTo(currentDate) <= 0
                      && reservation.getEndDate().compareTo(currentDate) >= 0)
              .collect(Collectors.toList());
      BigDecimal takenArea = dailyReservations
          .stream()
          .map(Reservation::getAreaInMeters)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      if (takenArea.compareTo(magazine.getAreaInMeters()) == 0
          || magazine.getMinAreaToRent().compareTo(area) > 0) {
        result.add(new DailyStateDto(id, date, AvailabilityState.FULL));
        continue;
      }
      if (magazine.getAreaInMeters().subtract(takenArea).compareTo(area) < 0) {
        result.add(new DailyStateDto(id, date, AvailabilityState.SOME));
        continue;
      }
      result.add(new DailyStateDto(id, date, AvailabilityState.AVAILABLE));
    }
    return result;
  }

  public List<Reservation> getUserActive(String userId) {
    return reservationRepository.findActiveReservationsByUser(userId);
  }

  public List<Reservation> getUserPast(String userId) {
    return reservationRepository.findPastReservationsByUser(userId);
  }

  public List<Reservation> getUserAll(String userId) {
    return reservationRepository.findAllByUser_Id(userId);
  }

  public boolean checkIfMagazineAvailable(@NotNull @Valid Magazine magazine,
                                          @NotNull LocalDate start, @NotNull LocalDate end,
                                          @NotNull @Min(0) BigDecimal area) {
    if (start.compareTo(end) > 0) {
      throw new ValidationException("End date must be greater or equal to start date");
    }

    if (magazine.getAreaInMeters().compareTo(area) < 0
        || magazine.getMinAreaToRent().compareTo(area) > 0
        || magazine.getStartDate().compareTo(start) > 0
        || magazine.getEndDate().compareTo(end) < 0
        || magazine.isDeleted()) {
      return false;
    }
    BigDecimal takenArea = getTakenArea(magazine.getId(), start, end);
    return magazine.getAreaInMeters().subtract(takenArea).compareTo(area) >= 0;
  }

  public BigDecimal getAvailableArea(@NotNull @Valid Magazine magazine, @NotNull LocalDate start,
                                     @NotNull LocalDate end) {
    if (start.compareTo(end) > 0) {
      throw new ValidationException("End date must be greater or equal to start date");
    }

    if (!checkIfMagazineAvailable(magazine, start, end, magazine.getMinAreaToRent())) {
      return BigDecimal.ZERO;
    }

    BigDecimal takenArea = getTakenArea(magazine.getId(), start, end);
    return magazine.getAreaInMeters().subtract(takenArea);
  }

  public TotalPrice getTotalPrice(@Valid ReservationDto reservation)
      throws MagazineNotFoundException {
    Magazine magazine = magazineService.getMagazine(reservation.getMagazineId());
    return new TotalPrice(
        magazine.getPricePerMeter(),
        reservation.getAreaInMeters(),
        reservation.getStartDate(),
        reservation.getEndDate()
    );
  }

  private BigDecimal getTakenArea(@NotNull Long magazineId, @NotNull LocalDate start,
                                  @NotNull LocalDate end) {
    if (start.compareTo(end) > 0) {
      throw new ValidationException("End date must be greater than or equal to end date");
    }

    List<Reservation> reservations = reservationRepository
        .findByMagazine_Id(magazineId)
        .stream()
        .filter(reservation -> (start.compareTo(reservation.getStartDate()) >= 0
            && start.compareTo(reservation.getEndDate()) <= 0)
            || (end.compareTo(reservation.getStartDate()) >= 0
            && end.compareTo(reservation.getEndDate()) <= 0)
            || (start.compareTo(reservation.getStartDate()) <= 0
            && end.compareTo(reservation.getEndDate()) >= 0)
            || (start.compareTo(reservation.getStartDate()) >= 0
            && end.compareTo(reservation.getEndDate()) <= 0))
        .collect(Collectors.toList());
    if (reservations.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return reservations
        .stream()
        .map(Reservation::getAreaInMeters)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
