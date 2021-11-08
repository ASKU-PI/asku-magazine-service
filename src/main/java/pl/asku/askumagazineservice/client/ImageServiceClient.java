package pl.asku.askumagazineservice.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import pl.asku.askumagazineservice.dto.client.imageservice.MagazinePictureDto;
import pl.asku.askumagazineservice.dto.client.imageservice.UserPictureDto;

@Service
public class ImageServiceClient {

  private final RestTemplate restTemplate;

  private final String baseUrl = "http://asku-image-service:8892";

  public ImageServiceClient(@Autowired RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public MagazinePictureDto uploadMagazinePictures(Long magazineId, MultipartFile[] files) {
    var path = "/magazine";

    UriComponentsBuilder pathBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
        .queryParam("id", magazineId);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    for (MultipartFile file : files) {
      body.add("picture", file.getResource());
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    MagazinePictureDto magazinePictureDto =
        restTemplate.postForObject(pathBuilder.toUriString(), requestEntity,
            MagazinePictureDto.class);
    if (magazinePictureDto == null) {
      magazinePictureDto = new MagazinePictureDto(magazineId, new ArrayList<>());
    }
    return magazinePictureDto;
  }

  public MagazinePictureDto getMagazinePictures(Long magazineId) {
    var path = "/magazine";

    UriComponentsBuilder pathBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
        .queryParam("id", magazineId);

    MagazinePictureDto magazinePictureDto = restTemplate.getForObject(pathBuilder.toUriString(),
        MagazinePictureDto.class);
    if (magazinePictureDto == null) {
      magazinePictureDto = new MagazinePictureDto(magazineId, new ArrayList<>());
    }
    return magazinePictureDto;
  }

  public UserPictureDto uploadUserPicture(String userId, MultipartFile file) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    body.add("picture", file.getResource());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    var path = "/profile";

    UriComponentsBuilder pathBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
        .queryParam("id", userId);

    UserPictureDto userPictureDto =
        restTemplate.postForObject(pathBuilder.toUriString(), requestEntity,
            UserPictureDto.class);
    if (userPictureDto == null) {
      userPictureDto = new UserPictureDto(null);
    }
    return userPictureDto;
  }

  public UserPictureDto getUserPicture(String userId) {
    var path = "/profile";

    UriComponentsBuilder pathBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
        .queryParam("id", userId);

    UserPictureDto userPictureDto = restTemplate.getForObject(pathBuilder.toUriString(),
        UserPictureDto.class);
    if (userPictureDto == null) {
      userPictureDto = new UserPictureDto(null);
    }
    return userPictureDto;
  }
}
