package pl.asku.askumagazineservice.util.validator;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;

@Service
@AllArgsConstructor
public class MagazineValidator {

  private final Validator validator;

  public boolean validate(MagazineDto magazineDto) {
    List<String> violations =
        validator.validate(magazineDto).stream().map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());

    if (magazineDto.getStartDate().compareTo(magazineDto.getEndDate()) >= 0) {
      violations.add("End date must be greater than start date");
    }

    if (magazineDto.getMinAreaToRent() == null
        && (magazineDto.getWhole() == null || !magazineDto.getWhole())) {
      violations.add("Min area to rent must be provided when not whole");
    }

    if (magazineDto.getMinAreaToRent() != null && magazineDto.getWhole() != null
        && magazineDto.getWhole()) {
      violations.add("Min area mustn't be provided when isWhole flag is true");
    }

    if (magazineDto.getMinAreaToRent() != null
        && magazineDto.getMinAreaToRent().compareTo(magazineDto.getAreaInMeters()) > 0) {
      violations.add("Min area to rent must be less than or equal total area");
    }

    if (violations.size() > 0) {
      throw new ValidationException(violations.toString());
    }

    return true;
  }
}
