package pl.asku.askumagazineservice.model.search;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.Geolocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class LocationFilter {
    private final BigDecimal defaultRadiusInKilometers = BigDecimal.valueOf(5.0f);
    private final BigDecimal kilometerToDegreeRatio = BigDecimal.valueOf(111.0f);
    private BigDecimal minLongitude;
    private BigDecimal maxLongitude;
    private BigDecimal minLatitude;
    private BigDecimal maxLatitude;

    public LocationFilter(
            BigDecimal minLongitude,
            BigDecimal maxLongitude,
            BigDecimal minLatitude,
            BigDecimal maxLatitude) {
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
    }

    public LocationFilter(String location, GeocodingClient geocodingClient) throws LocationNotFoundException, LocationIqRequestFailedException {
        Geolocation center = geocodingClient.getGeolocation(location);

        setBoundariesInRadius(center, kilometersToDegrees(defaultRadiusInKilometers));
    }

    public LocationFilter(String location, BigDecimal radiusInKilometers, GeocodingClient geocodingClient) throws LocationNotFoundException, LocationIqRequestFailedException {
        Geolocation center = geocodingClient.getGeolocation(location);

        setBoundariesInRadius(center, kilometersToDegrees(radiusInKilometers));
    }

    private void setBoundariesInRadius(Geolocation center, BigDecimal radiusInDegrees) {
        minLongitude = center.getLongitude().subtract(radiusInDegrees);
        maxLongitude = center.getLongitude().add(radiusInDegrees);

        minLatitude = center.getLatitude().subtract(radiusInDegrees);
        maxLatitude = center.getLatitude().add(radiusInDegrees);
    }

    private BigDecimal kilometersToDegrees(BigDecimal kilometers) {
        return kilometers.divide(kilometerToDegreeRatio, RoundingMode.HALF_EVEN);
    }
}
