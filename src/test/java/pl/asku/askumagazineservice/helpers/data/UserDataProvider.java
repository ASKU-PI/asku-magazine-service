package pl.asku.askumagazineservice.helpers.data;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.dto.user.UserDto;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.repository.UserRepository;

@Component
@AllArgsConstructor
public class UserDataProvider {

  private final UserRepository userRepository;

  public UserDto userDto(String email, String phoneNumber) {
    return UserDto.builder()
        .firstName("Test")
        .lastName("Test")
        .phoneNumber(phoneNumber)
        .email(email)
        .address("Test street test city test country")
        .birthDate(LocalDate.of(1999, 1, 1))
        .build();
  }

  public User user(String email, String phoneNumber) {
    User user = User.builder()
        .id(email)
        .firstName("Test")
        .lastName("Test")
        .phoneNumber(phoneNumber)
        .email(email)
        .address("Test street test city test country")
        .birthDate(LocalDate.of(1999, 1, 1))
        .build();

    return userRepository.save(user);
  }
}
