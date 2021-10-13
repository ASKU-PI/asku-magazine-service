package pl.asku.askumagazineservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import pl.asku.askumagazineservice.dto.imageservice.MagazinePictureDto;

import java.io.IOException;

@Service
public class ImageServiceClient {

    private final RestTemplate restTemplate;

    private final String baseUrl = "http://asku-image-service:8892";

    public ImageServiceClient(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MagazinePictureDto uploadMagazinePictures(Long magazineId, MultipartFile[] files) throws IOException {
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

        return restTemplate.postForObject(pathBuilder.toUriString(), requestEntity, MagazinePictureDto.class);
    }

    public MagazinePictureDto getMagazinePictures(Long magazineId) {
        var path = "/magazine";

        UriComponentsBuilder pathBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                .queryParam("id", magazineId);

        return restTemplate.getForObject(pathBuilder.toUriString(), MagazinePictureDto.class);
    }
}
