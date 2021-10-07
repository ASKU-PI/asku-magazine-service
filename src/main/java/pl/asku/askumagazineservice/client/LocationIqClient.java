package pl.asku.askumagazineservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.asku.askumagazineservice.dto.locationiq.LocationIqResponse;
import pl.asku.askumagazineservice.model.Geolocation;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class LocationIqClient implements GeocodingClient {

    private final String baseUrl = "https://eu1.locationiq.com/v1/";
    private final RestTemplate restTemplate;
    @Value("${location-iq.api-key}")
    private String accessKey;

    public LocationIqClient(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Geolocation> getGeolocation(String country, String city, String street, String building) {
        String search = new StringBuilder(country)
                .append(" ")
                .append(city)
                .append(" ")
                .append(street)
                .append(" ")
                .append(building)
                .toString();

        return getGeolocation(search);
    }

    public Optional<Geolocation> getGeolocation(String search) {
        String url = new StringBuilder(baseUrl)
                .append("search.php?key=")
                .append(accessKey)
                .append("&q=")
                .append(search)
                .append("&format=json")
                .toString();

        LocationIqResponse[] locationIqResponse = restTemplate.
                getForObject(url, LocationIqResponse[].class);

        return locationIqResponse == null || locationIqResponse.length > 0
                ? Optional.of(
                new Geolocation(
                        new BigDecimal(locationIqResponse[0].getLon()),
                        new BigDecimal(locationIqResponse[0].getLat())))
                : Optional.empty();
    }
}
