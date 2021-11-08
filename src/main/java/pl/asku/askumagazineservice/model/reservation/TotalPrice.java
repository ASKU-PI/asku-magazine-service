package pl.asku.askumagazineservice.model.reservation;

import static java.time.temporal.ChronoUnit.DAYS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class TotalPrice {

  private final BigDecimal serviceFeeMultiplier =
      BigDecimal.valueOf(0.05f).setScale(2, RoundingMode.HALF_EVEN);
  private final BigDecimal taxMultiplier =
      BigDecimal.valueOf(0.1f).setScale(2, RoundingMode.HALF_EVEN);

  private final BigDecimal price;
  private final BigDecimal serviceFee;
  private final BigDecimal tax;
  private final BigDecimal totalPrice;

  public TotalPrice(BigDecimal price) {
    this.price = price;
    this.serviceFee = price.multiply(serviceFeeMultiplier);
    this.tax = price.multiply(taxMultiplier);
    this.totalPrice = price.add(serviceFee).add(tax);
  }

  public TotalPrice(BigDecimal pricePerMeter, BigDecimal areaInMeters, LocalDate startDate,
                    LocalDate endDate) {
    this.price = pricePerMeter
        .multiply(areaInMeters)
        .multiply(BigDecimal.valueOf(DAYS.between(startDate, endDate.plusDays(1))))
        .setScale(2, RoundingMode.HALF_EVEN);
    this.serviceFee = price.multiply(serviceFeeMultiplier)
        .setScale(2, RoundingMode.HALF_EVEN);
    this.tax = price.multiply(taxMultiplier).setScale(2, RoundingMode.HALF_EVEN);
    this.totalPrice = price.add(serviceFee).add(tax).setScale(2, RoundingMode.HALF_EVEN);
  }
}
