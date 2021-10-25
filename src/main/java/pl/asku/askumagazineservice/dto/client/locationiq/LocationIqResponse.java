package pl.asku.askumagazineservice.dto.client.locationiq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationIqResponse implements Serializable {
    String lat;
    String lon;
}
