package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.client.imageservice.PictureData;
import pl.asku.askumagazineservice.dto.user.UserDto;
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

  public User updateUser(User user, UserDto userDto) {
    user.setAddress(userDto.getAddress());
    user.setBirthDate(user.getBirthDate());
    user.setFirstName(user.getFirstName());
    user.setLastName(user.getLastName());
    user.setPhoneNumber(user.getPhoneNumber());

    return user;
  }
}
