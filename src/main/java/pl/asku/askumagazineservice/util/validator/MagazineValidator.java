package pl.asku.askumagazineservice.util.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MagazineValidator {

    private final Validator validator;

    public boolean validate(MagazineDto magazineDto) {
        List<String> violations =
                validator.validate(magazineDto).stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());

        if (magazineDto.getStartDate().compareTo(magazineDto.getEndDate()) >= 0)
            violations.add("End date must be greater than end date");

        if (magazineDto.getMinAreaToRent().compareTo(magazineDto.getAreaInMeters()) > 0)
            violations.add("Min area to rent must be less than or equal total area");

        if (violations.size() > 0) {
            throw new ValidationException(violations.toString());
        }

        return true;
    }
}
