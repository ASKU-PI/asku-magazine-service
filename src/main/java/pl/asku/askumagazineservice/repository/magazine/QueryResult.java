package pl.asku.askumagazineservice.repository.magazine;

import lombok.AllArgsConstructor;
import pl.asku.askumagazineservice.model.magazine.Magazine;

import java.math.BigDecimal;

@AllArgsConstructor
public class QueryResult {
    public Magazine magazine;
    public BigDecimal totalArea;
}
