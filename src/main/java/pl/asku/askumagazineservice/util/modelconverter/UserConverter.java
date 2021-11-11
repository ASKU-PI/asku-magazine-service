package pl.asku.askumagazineservice.util.modelconverter;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.client.imageservice.PictureData;
import pl.asku.askumagazineservice.dto.user.UserDto;
import pl.asku.askumagazineservice.dto.user.UserRegisterDto;
import pl.asku.askumagazineservice.dto.user.UserUpdateDto;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;

@Service
@AllArgsConstructor
public class UserConverter {

  private final ImageServiceClient imageServiceClient;
  @Lazy
  private final MagazineService magazineService;
  @Lazy
  private final ReservationService reservationService;
  @Lazy
  private final ReviewService reviewService;

  public UserDto toDto(User user) {

    PictureData avatar;
    if (user.getId() == null) {
      avatar = null;
    } else {
      avatar = imageServiceClient.getUserPicture(user.getId()).getPhoto();
    }

    Long magazinesCount = (long) magazineService.getActiveByOwner(user.getId()).size();
    Long reservationsCount = (long) reservationService.getUserAll(user.getId()).size();
    Long reviewsCount = (long) reviewService.getUserReviewsNumber(user.getId());
    BigDecimal averageRating = reviewService.getUserAverageRating(user.getId());

    return UserDto.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phoneNumber(user.getPhoneNumber())
        .avatar(avatar)
        .email(user.getEmail())
        .birthDate(user.getBirthDate())
        .address(user.getAddress())
        .averageRating(averageRating)
        .ownedSpacesCount(magazinesCount)
        .reservationsCount(reservationsCount)
        .reviewsCount(reviewsCount)
        .build();
  }

  public User toUser(UserRegisterDto userDto) {
    return User.builder()
        .id(userDto.getEmail())
        .firstName(userDto.getFirstName())
        .lastName(userDto.getLastName())
        .phoneNumber(userDto.getPhoneNumber())
        .email(userDto.getEmail())
        .birthDate(userDto.getBirthDate())
        .address(userDto.getAddress())
        .build();
  }

  public User toUser(UserDto userDto) {
    return User.builder()
        .id(userDto.getEmail())
        .firstName(userDto.getFirstName())
        .lastName(userDto.getLastName())
        .phoneNumber(userDto.getPhoneNumber())
        .email(userDto.getEmail())
        .birthDate(userDto.getBirthDate())
        .address(userDto.getAddress())
        .build();
  }

  public User updateUser(User user, UserUpdateDto userDto) {
    if (userDto.getAddress() != null) {
      user.setAddress(userDto.getAddress());
    }
    if (userDto.getBirthDate() != null) {
      user.setBirthDate(userDto.getBirthDate());
    }
    if (userDto.getFirstName() != null) {
      user.setFirstName(userDto.getFirstName());
    }
    if (userDto.getLastName() != null) {
      user.setLastName(userDto.getLastName());
    }
    if (userDto.getPhoneNumber() != null) {
      user.setPhoneNumber(userDto.getPhoneNumber());
    }

    return user;
  }
}
