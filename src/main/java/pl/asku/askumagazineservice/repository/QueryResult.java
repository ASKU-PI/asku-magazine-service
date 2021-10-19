package pl.asku.askumagazineservice.repository;

import lombok.AllArgsConstructor;
import pl.asku.askumagazineservice.model.Magazine;

import java.math.BigDecimal;

@AllArgsConstructor
public class QueryResult {
    public Magazine magazine;
    public BigDecimal totalArea;
}
