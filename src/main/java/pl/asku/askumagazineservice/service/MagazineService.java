package pl.asku.askumagazineservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineBoundaryValuesDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineCreateDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineUpdateDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.MagazineSearchResult;
import pl.asku.askumagazineservice.model.magazine.search.SortOptions;
import pl.asku.askumagazineservice.repository.magazine.MagazineRepository;
import pl.asku.askumagazineservice.util.modelconverter.MagazineConverter;
import pl.asku.askumagazineservice.util.validator.MagazineValidator;

@Service
@Validated
@AllArgsConstructor
public class MagazineService {

  private final MagazineRepository magazineRepository;
  private final ImageServiceClient imageServiceClient;
  private final GeocodingClient geocodingClient;
  @Lazy
  private final MagazineConverter magazineConverter;
  private final MagazineValidator magazineValidator;

  @Transactional
  public Magazine addMagazine(
      @Valid @NotNull MagazineCreateDto magazineDto,
      @Valid @NotNull User user,
      MultipartFile[] photos)
      throws LocationNotFoundException, LocationIqRequestFailedException {

    magazineValidator.validate(magazineDto, photos);

    Geolocation geolocation = geocodingClient.getGeolocation(
        magazineDto.getCountry(),
        magazineDto.getCity(),
        magazineDto.getStreet(),
        magazineDto.getBuilding()
    );

    Magazine magazine = magazineConverter.toMagazine(magazineDto, user, geolocation);

    magazine = magazineRepository.save(magazine);

    if (photos != null && photos.length > 0) {
      imageServiceClient.uploadMagazinePictures(magazine.getId(), photos);
    }

    return magazine;
  }

  @Transactional
  public Magazine updateMagazine(
      @Valid @NotNull Magazine magazine,
      @Valid @NotNull MagazineUpdateDto magazineDto,
      List<String> toDeletePhotosIds,
      MultipartFile[] toAddPhotos
  ) {
    magazineValidator.validate(magazineDto, toAddPhotos);

    Magazine updatedMagazine = magazineConverter.updateMagazine(magazine, magazineDto);
    updatedMagazine = magazineRepository.save(updatedMagazine);

    if (toAddPhotos != null && toAddPhotos.length > 0) {
      imageServiceClient.uploadMagazinePictures(magazine.getId(), toAddPhotos);
    }

    return updatedMagazine;
  }

  public Magazine setAvailable(
      @Valid @NotNull Magazine magazine,
      @NotNull Boolean available
  ) {
    magazine.setAvailable(available);
    return magazineRepository.save(magazine);
  }

  @Transactional
  public Magazine deleteMagazine(@Valid Magazine magazine) throws MagazineNotFoundException {
    magazineRepository.deleteById(magazine.getId());
    return getMagazine(magazine.getId());
  }

  public Magazine getMagazine(@NotNull Long id) throws MagazineNotFoundException {
    Optional<Magazine> magazine = magazineRepository.findById(id);
    if (magazine.isEmpty()) {
      throw new MagazineNotFoundException();
    }
    return magazine.get();
  }

  public List<Magazine> getActiveByOwner(@NotNull String ownerId) {
    return magazineRepository.findAllActiveByOwner(ownerId);
  }

  public List<Magazine> getAllNotDeletedByOwner(@NotNull String ownerId) {
    return magazineRepository.findAllByOwner_IdAndDeleted(ownerId, false);
  }

  public MagazineSearchResult searchMagazines(
      @Min(1) Integer page,
      @NotNull MagazineFilters filters,
      SortOptions sortOptions) throws UserNotFoundException {
    if (sortOptions == null) {
      return magazineRepository.search(filters, PageRequest.of(page - 1, 20));
    } else {
      return magazineRepository.search(filters,
          PageRequest.of(page - 1, 20, sortOptions.getSort()));
    }
  }

  public MagazineBoundaryValuesDto getBoundaryValues() {
    return magazineRepository.getBoundaryValues();
  }

  public BigDecimal maxArea() throws MagazineNotFoundException {
    Magazine maxAreaMagazine = magazineRepository.findFirstByOrderByAreaInMetersDesc();
    if (maxAreaMagazine == null) {
      throw new MagazineNotFoundException();
    }
    return maxAreaMagazine.getAreaInMeters();
  }

}
