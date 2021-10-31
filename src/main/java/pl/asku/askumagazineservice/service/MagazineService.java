package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.SortOptions;
import pl.asku.askumagazineservice.repository.magazine.MagazineRepository;
import pl.asku.askumagazineservice.util.modelconverter.MagazineConverter;
import pl.asku.askumagazineservice.util.validator.MagazineValidator;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;
    private final ImageServiceClient imageServiceClient;
    private final GeocodingClient geocodingClient;
    private final MagazineConverter magazineConverter;
    private final MagazineValidator magazineValidator;

    @Transactional
    public Magazine addMagazine(@Valid MagazineDto magazineDto, @NotNull @NotBlank String username,
                                MultipartFile[] photos)
            throws LocationNotFoundException, LocationIqRequestFailedException {
        magazineValidator.validate(magazineDto);

        Magazine magazine = magazineConverter.toMagazine(magazineDto);

        magazine.setOwnerId(username);

        Geolocation geolocation = geocodingClient.getGeolocation(
                magazine.getCountry(),
                magazine.getCity(),
                magazine.getStreet(),
                magazine.getBuilding()
        );

        magazine.setLongitude(geolocation.getLongitude());
        magazine.setLatitude(geolocation.getLatitude());

        magazine = magazineRepository.save(magazine);

        if (photos != null && photos.length > 0)
            imageServiceClient.uploadMagazinePictures(magazine.getId(), photos);

        return magazine;
    }

    public Magazine getMagazineDetails(@NotNull Long id) throws MagazineNotFoundException {
        Optional<Magazine> magazine = magazineRepository.findById(id);
        if (magazine.isEmpty()) throw new MagazineNotFoundException();
        return magazine.get();
    }

    public List<Magazine> getActiveByOwner(@NotNull String ownerId) {
        return magazineRepository.findAllActiveByOwner(ownerId);
    }

    public List<Magazine> searchMagazines(
            @Min(1) Integer page,
            @NotNull MagazineFilters filters,
            SortOptions sortOptions) throws UserNotFoundException {
        if (sortOptions == null) {
            return magazineRepository.search(filters, PageRequest.of(page - 1, 20));
        } else {
            return magazineRepository.search(filters, PageRequest.of(page - 1, 20, sortOptions.getSort()));
        }
    }

    public BigDecimal getTotalPrice(@NotNull @Valid Magazine magazine, @NotNull LocalDate start, @NotNull LocalDate end,
                                    @NotNull @Min(0) BigDecimal area) {
        if (start.compareTo(end) >= 0)
            throw new ValidationException("End date must be greater than end date");

        int dateDifference = start.until(end).getDays();
        return area.multiply(magazine.getPricePerMeter()).multiply(BigDecimal.valueOf(dateDifference));
    }

    public BigDecimal maxArea() throws MagazineNotFoundException {
        Magazine maxAreaMagazine = magazineRepository.findFirstByOrderByAreaInMetersDesc();
        if (maxAreaMagazine == null) throw new MagazineNotFoundException();
        return maxAreaMagazine.getAreaInMeters();
    }

}
