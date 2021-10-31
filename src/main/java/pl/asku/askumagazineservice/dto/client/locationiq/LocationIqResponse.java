package pl.asku.askumagazineservice.dto.client.locationiq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationIqResponse implements Serializable {
  String lat;
  String lon;
}
