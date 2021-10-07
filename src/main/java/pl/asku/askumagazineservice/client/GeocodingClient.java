package pl.asku.askumagazineservice.client;

import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.model.Geolocation;

import java.util.Optional;

@Service
public interface GeocodingClient {

    Optional<Geolocation> getGeolocation(String country, String city, String street, String building);

    Optional<Geolocation> getGeolocation(String search);
}
