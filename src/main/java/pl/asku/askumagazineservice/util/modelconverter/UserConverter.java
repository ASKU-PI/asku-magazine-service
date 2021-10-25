package pl.asku.askumagazineservice.util.modelconverter;

import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.UserDto;
import pl.asku.askumagazineservice.model.User;

@Service
public class UserConverter {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
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
