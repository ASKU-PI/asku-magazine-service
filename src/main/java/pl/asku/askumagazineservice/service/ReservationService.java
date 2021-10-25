package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.repository.ReservationRepository;
import pl.asku.askumagazineservice.util.validator.ReservationValidator;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class ReservationService {

    private final ReservationValidator reservationValidator;
    private final MagazineService magazineService;
    private final ReservationRepository reservationRepository;

    public Reservation addReservation(@Valid ReservationDto reservationDto,
                                      @NotNull @NotBlank String username) throws MagazineNotAvailableException,
            MagazineNotFoundException {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(reservationDto.getMagazineId());
        if (magazine.isEmpty()) throw new MagazineNotFoundException();
        return addReservation(magazine.get(), reservationDto, username);
    }

    @Transactional
    public Reservation addReservation(@NotNull @Valid Magazine magazine, @NotNull @Valid ReservationDto reservationDto,
                                      @NotNull @NotBlank String username) throws MagazineNotAvailableException {
        reservationValidator.validate(reservationDto);

        if (reservationDto.getStartDate().compareTo(reservationDto.getEndDate()) >= 0 ||
                !checkIfMagazineAvailable(
                        magazine,
                        reservationDto.getStartDate(),
                        reservationDto.getEndDate(),
                        reservationDto.getAreaInMeters())) {
            throw new MagazineNotAvailableException();
        }
        Reservation reservation = Reservation.builder()
                .userId(username)
                .startDate(reservationDto.getStartDate())
                .endDate(reservationDto.getEndDate())
                .areaInMeters(reservationDto.getAreaInMeters())
                .magazine(magazine)
                .build();
        return reservationRepository.save(reservation);
    }

    public boolean checkIfMagazineAvailable(@NotNull @Valid Magazine magazine,
                                            @NotNull LocalDate start, @NotNull LocalDate end,
                                            @NotNull @Min(0) BigDecimal area) {
        if (start.compareTo(end) >= 0)
            throw new ValidationException("End date must be greater than end date");

        if (magazine.getAreaInMeters().compareTo(area) < 0 || magazine.getMinAreaToRent().compareTo(area) > 0
                || magazine.getStartDate().compareTo(start) > 0 ||
                magazine.getEndDate().compareTo(end) < 0) {
            return false;
        }
        BigDecimal takenArea = getTakenArea(magazine.getId(), start, end);
        return magazine.getAreaInMeters().subtract(takenArea).compareTo(area) >= 0;
    }

    private BigDecimal getTakenArea(@NotNull Long magazineId, @NotNull LocalDate start, @NotNull LocalDate end) {
        if (start.compareTo(end) >= 0)
            throw new ValidationException("End date must be greater than end date");

        List<Reservation> reservations = reservationRepository
                .findByMagazine_Id(magazineId)
                .stream()
                .filter(reservation -> (start.compareTo(reservation.getStartDate()) >= 0
                        && start.compareTo(reservation.getEndDate()) <= 0) ||
                        (end.compareTo(reservation.getStartDate()) >= 0
                                && end.compareTo(reservation.getEndDate()) <= 0) ||
                        (start.compareTo(reservation.getStartDate()) <= 0
                                && end.compareTo(reservation.getEndDate()) >= 0) ||
                        (start.compareTo(reservation.getStartDate()) >= 0
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
