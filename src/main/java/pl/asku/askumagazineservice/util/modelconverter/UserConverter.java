package pl.asku.askumagazineservice.util.modelconverter;

import java.util.ArrayList;
import java.util.List;
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
        .build();
  }

  public User toUser(UserDto userDto) {
    return User.builder()
        .id(userDto.getId())
        .firstName(userDto.getFirstName())
        .lastName(userDto.getLastName())
        .phoneNumber(userDto.getPhoneNumber())
        .email(userDto.getEmail())
        .build();
  }
}
