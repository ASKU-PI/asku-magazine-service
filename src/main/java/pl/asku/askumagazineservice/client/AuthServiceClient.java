package pl.asku.askumagazineservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.asku.askumagazineservice.dto.client.authservice.RegisterDto;
import pl.asku.askumagazineservice.dto.client.authservice.RegisterResponseDto;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookRegisterDto;
import pl.asku.askumagazineservice.dto.client.authservice.facebook.FacebookUserDto;

@Service
public class AuthServiceClient {

  private final RestTemplate restTemplate;

  private final String baseUrl = "http://asku-auth-service:8889";

  public AuthServiceClient(@Autowired RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public RegisterResponseDto register(String email, String password) {
    var path = "/api/register";

    return restTemplate.postForObject(
        baseUrl + path,
        RegisterDto.builder().email(email).password(password).firstName("delete").lastName("delete")
            .build(),
        RegisterResponseDto.class
    );
  }

  public FacebookUserDto register(FacebookRegisterDto facebookRegisterDto) {
    var path = "/api/facebook/register";

    return restTemplate.postForObject(
        baseUrl + path,
        facebookRegisterDto,
        FacebookUserDto.class
    );
  }
}
