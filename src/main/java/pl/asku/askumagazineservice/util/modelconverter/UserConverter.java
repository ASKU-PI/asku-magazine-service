package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.client.imageservice.PictureData;
import pl.asku.askumagazineservice.dto.user.UserDto;
import pl.asku.askumagazineservice.dto.user.UserUpdateDto;
import pl.asku.askumagazineservice.model.User;

@Service
@AllArgsConstructor
public class UserConverter {

  private final ImageServiceClient imageServiceClient;

  public UserDto toDto(User user) {

    PictureData avatar;
    if (user.getId() == null) {
      avatar = null;
    } else {
      avatar = imageServiceClient.getUserPicture(user.getId()).getPhoto();
    }

    return UserDto.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phoneNumber(user.getPhoneNumber())
        .avatar(avatar)
        .email(user.getEmail())
        .birthDate(user.getBirthDate())
        .address(user.getAddress())
        .build();
  }

  public User toUser(UserDto userDto) {
    return User.builder()
        .id(userDto.getId())
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
