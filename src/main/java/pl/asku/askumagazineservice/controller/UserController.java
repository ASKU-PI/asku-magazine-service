package pl.asku.askumagazineservice.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookRegisterDto;
import pl.asku.askumagazineservice.dto.user.UserDto;
import pl.asku.askumagazineservice.dto.user.UserRegisterDto;
import pl.asku.askumagazineservice.dto.user.UserUpdateDto;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.security.policy.UserPolicy;
import pl.asku.askumagazineservice.service.UserService;
import pl.asku.askumagazineservice.util.modelconverter.UserConverter;
import pl.asku.askumagazineservice.util.modelconverter.UserPersonalConverter;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserConverter userConverter;
  private final UserPersonalConverter userPersonalConverter;
  private final UserPolicy userPolicy;

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/user")
  public ResponseEntity<Object> createUser(
      @ModelAttribute @Valid UserRegisterDto userDto,
      @RequestPart(value = "files", required = false) MultipartFile avatar
  ) {
    User user = userService.addUser(userDto, avatar);
    return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(user));
  }

  @PostMapping("/facebook/user")
  public ResponseEntity<Object> createFacebookUser(
      @RequestBody @Valid FacebookRegisterDto facebookRegisterDto) {
    User user = userService.addUser(facebookRegisterDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(user));
  }

  @PatchMapping("/user/{id}")
  public ResponseEntity<Object> updateUser(
      @PathVariable String id,
      @ModelAttribute @Valid UserUpdateDto userDto,
      @RequestPart(value = "files", required = false) MultipartFile avatar,
      Authentication authentication
  ) {
    try {
      User user = userService.getUser(id);

      if (!userPolicy.updateUser(authentication, user)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to update this user");
      }

      return ResponseEntity.status(HttpStatus.OK).body(
          userConverter.toDto(userService.updateUser(
              user, userDto, avatar
          )));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @PatchMapping("/user")
  public ResponseEntity<Object> updateUser(
      @ModelAttribute @Valid UserUpdateDto userDto,
      @RequestPart(value = "files", required = false) MultipartFile avatar,
      Authentication authentication
  ) {
    if (authentication == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You are not authenticated");
    }

    try {
      User user = userService.getUser(authentication.getName());

      return ResponseEntity.status(HttpStatus.OK).body(
          userConverter.toDto(userService.updateUser(
              user, userDto, avatar
          )));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<Object> getUser(@PathVariable @NotNull String id,
                                        Authentication authentication) {
    if (!userPolicy.getUser(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You're not authorized to get users");
    }
    try {
      User user = userService.getUser(id);
      return ResponseEntity.status(HttpStatus.OK).body(userConverter.toDto(user));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/user")
  public ResponseEntity<Object> getCurrentUser(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You're not authenticated!");
    }
    try {
      User user = userService.getUser(authentication.getName());
      return ResponseEntity.status(HttpStatus.OK).body(userConverter.toDto(user));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/user-profile/{id}")
  public ResponseEntity<Object> getUserProfile(@PathVariable @NotNull String id) {
    try {
      User user = userService.getUser(id);
      return ResponseEntity.status(HttpStatus.OK).body(userConverter.toDto(user));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/user-profile")
  public ResponseEntity<Object> getUserProfile(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You're not authenticated!");
    }
    try {
      User user = userService.getUser(authentication.getName());
      return ResponseEntity.status(HttpStatus.OK).body(userConverter.toDto(user));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/user-personal/{id}")
  public ResponseEntity<Object> getUserPersonal(@PathVariable @NotNull String id,
                                                Authentication authentication) {
    try {
      User user = userService.getUser(id);

      if (!userPolicy.getUserPersonal(authentication, user)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You're not authorized to get this user");
      }

      return ResponseEntity.status(HttpStatus.OK).body(userPersonalConverter.toPersonalDto(user));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/user-personal")
  public ResponseEntity<Object> getMePersonal(Authentication authentication) {
    try {
      if (authentication == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in!");
      }

      User user = userService.getUser(authentication.getName());

      return ResponseEntity.status(HttpStatus.OK).body(userPersonalConverter.toPersonalDto(user));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @DeleteMapping("/user-avatar/{id}")
  public ResponseEntity<Object> deleteUserAvatar(@PathVariable @NotNull String id, Authentication authentication) {
    try {
      User user = userService.getUser(id);

      if (!userPolicy.updateUser(authentication, user)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to update this user");
      }

      userService.deleteUserAvatar(user);

      return ResponseEntity.status(HttpStatus.OK).body("Avatar deleted");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @DeleteMapping("/user-avatar")
  public ResponseEntity<Object> deleteUserAvatar(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You are not authenticated");
    }

    try {
      User user = userService.getUser(authentication.getName());

      if (!userPolicy.updateUser(authentication, user)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to update this user");
      }

      userService.deleteUserAvatar(user);

      return ResponseEntity.status(HttpStatus.OK).body("Avatar deleted");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

}
