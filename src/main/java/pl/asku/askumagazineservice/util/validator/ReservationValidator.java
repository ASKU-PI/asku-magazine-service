package pl.asku.askumagazineservice.util.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationValidator {

    private final Validator validator;

    public boolean validate(ReservationDto reservationDto) {
        List<String> violations =
                validator.validate(reservationDto).stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());

        if (reservationDto.getStartDate().compareTo(reservationDto.getEndDate()) > 0)
            violations.add("End date must be greater than or equal to start date");

        if (violations.size() > 0) {
            throw new ValidationException(violations.toString());
        }

        return true;
    }
}
