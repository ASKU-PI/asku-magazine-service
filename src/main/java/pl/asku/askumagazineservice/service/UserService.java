package pl.asku.askumagazineservice.service;

import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.AuthServiceClient;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookRegisterDto;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookUserDto;
import pl.asku.askumagazineservice.dto.user.UserDto;
import pl.asku.askumagazineservice.dto.user.UserRegisterDto;
import pl.asku.askumagazineservice.dto.user.UserUpdateDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.repository.UserRepository;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

@Service
@Validated
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final AuthServiceClient authServiceClient;
  private final ImageServiceClient imageServiceClient;

  public User addUser(@Valid @NotNull UserRegisterDto userDto, MultipartFile avatar) {
    authServiceClient.register(userDto.getEmail(), userDto.getPassword());
    User user = userRepository.save(userConverter.toUser(userDto));

    if (avatar != null) {
      imageServiceClient.uploadUserPicture(user.getId(), avatar);
    }

    return user;
  }

  public User addUser(@Valid @NotNull FacebookRegisterDto facebookRegisterDto) {
    FacebookUserDto facebookUserDto = authServiceClient.register(facebookRegisterDto);
    User user = User.builder()
        .id(facebookUserDto.getId())
        .firstName(facebookUserDto.getFirstName())
        .lastName(facebookUserDto.getLastName())
        .email(facebookUserDto.getEmail())
        .build();
    return userRepository.save(user);
  }

  public User updateUser(
      @Valid @NotNull User user,
      @Valid @NotNull UserUpdateDto userDto,
      MultipartFile avatar) {
    User updatedUser = userConverter.updateUser(user, userDto);

    if (avatar != null) {
      imageServiceClient.uploadUserPicture(user.getId(), avatar);
    }

    return userRepository.save(updatedUser);
  }

  public User getUser(@NotNull String id) throws UserNotFoundException {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }
    return user.get();
  }
}
