package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.client.AuthServiceClient;
import pl.asku.askumagazineservice.dto.UserDto;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookRegisterDto;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookUserDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.repository.UserRepository;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final AuthServiceClient authServiceClient;

    public User addUser(@Valid @NotNull UserDto userDto) {
        authServiceClient.register(userDto.getEmail(), userDto.getPassword());
        userDto.setId(userDto.getEmail());
        User user = userConverter.toUser(userDto);
        return userRepository.save(user);
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

    public User getUser(@NotNull String id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) throw new UserNotFoundException();
        return user.get();
    }
}
