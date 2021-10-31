package pl.asku.askumagazineservice.repository.magazine;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import pl.asku.askumagazineservice.model.magazine.Magazine;

@AllArgsConstructor
public class QueryResult {
  public Magazine magazine;
  public BigDecimal totalArea;
}
