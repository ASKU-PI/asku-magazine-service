package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.UserDto;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookRegisterDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.security.policy.UserPolicy;
import pl.asku.askumagazineservice.service.UserService;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final UserPolicy userPolicy;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        User user = userService.addUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(user));
    }

    @PostMapping("/facebook/user")
    public ResponseEntity<Object> createFacebookUser(@RequestBody @Valid FacebookRegisterDto facebookRegisterDto) {
        User user = userService.addUser(facebookRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(user));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUser(@PathVariable @NotNull String id, Authentication authentication) {
        if(!userPolicy.getUser(authentication)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You're not authorized to get users");
        }
        try {
            User user = userService.getUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(userConverter.toDto(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
