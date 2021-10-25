package pl.asku.askumagazineservice.helpers.data;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.dto.UserDto;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.repository.UserRepository;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

@Component
@AllArgsConstructor
public class UserDataProvider {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserDto getUser(String identifier) {
        UserDto userDto = UserDto.builder()
                .id(identifier)
                .firstName("Test")
                .lastName("Test")
                .phoneNumber("666666666")
                .email(identifier)
                .build();

        User user = userConverter.toUser(userDto);
        return userConverter.toDto(userRepository.save(user));
    }
}
