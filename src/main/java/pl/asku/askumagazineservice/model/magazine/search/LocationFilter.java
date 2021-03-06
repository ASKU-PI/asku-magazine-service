package pl.asku.askumagazineservice.model.magazine.search;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.magazine.Geolocation;

@Getter
public class LocationFilter {
  private final BigDecimal defaultRadiusInKilometers = BigDecimal.valueOf(15.0f);
  private final BigDecimal kilometerToDegreeRatio = BigDecimal.valueOf(111.0f);
  private final Geolocation mapCenter;
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

    if (minLongitude != null && maxLongitude != null
        && minLatitude != null && maxLatitude != null) {
      this.mapCenter = Geolocation.builder()
          .latitude(minLatitude.add(maxLatitude)
              .divide(BigDecimal.valueOf(2.0f), 10, RoundingMode.HALF_EVEN))
          .longitude(minLongitude.add(maxLongitude)
              .divide(BigDecimal.valueOf(2.0f), 10, RoundingMode.HALF_EVEN))
          .build();
    } else {
      this.mapCenter = null;
    }
  }

  public LocationFilter(String location, GeocodingClient geocodingClient)
      throws LocationNotFoundException,
      LocationIqRequestFailedException {
    Geolocation center = geocodingClient.getGeolocation(location);
    this.mapCenter = center;
    setBoundariesInRadius(center, kilometersToDegrees(defaultRadiusInKilometers));
  }

  public LocationFilter(String location, BigDecimal radiusInKilometers,
                        GeocodingClient geocodingClient)
      throws LocationNotFoundException, LocationIqRequestFailedException {
    Geolocation center = geocodingClient.getGeolocation(location);
    this.mapCenter = center;
    setBoundariesInRadius(center, kilometersToDegrees(radiusInKilometers));
  }

  private void setBoundariesInRadius(Geolocation center, BigDecimal radiusInDegrees) {
    minLongitude = center.getLongitude().subtract(radiusInDegrees);
    maxLongitude = center.getLongitude().add(radiusInDegrees);

    minLatitude = center.getLatitude().subtract(radiusInDegrees);
    maxLatitude = center.getLatitude().add(radiusInDegrees);
  }

  private BigDecimal kilometersToDegrees(BigDecimal kilometers) {
    return kilometers.divide(kilometerToDegreeRatio, 10, RoundingMode.HALF_EVEN);
  }
}
