package pl.asku.askumagazineservice.client;

import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.magazine.Geolocation;

@Service
public interface GeocodingClient {

  Geolocation getGeolocation(String country, String city, String street, String building)
      throws LocationNotFoundException, LocationIqRequestFailedException;

  Geolocation getGeolocation(String search)
      throws LocationIqRequestFailedException, LocationNotFoundException;
}
