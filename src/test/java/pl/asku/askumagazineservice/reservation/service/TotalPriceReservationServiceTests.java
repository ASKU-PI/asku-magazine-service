package pl.asku.askumagazineservice.reservation.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReservationDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.TotalPrice;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

public class TotalPriceReservationServiceTests extends ReservationServiceTestBase {

  @Autowired
  public TotalPriceReservationServiceTests(MagazineService magazineService,
                                           MagazineDataProvider magazineDataProvider,
                                           ReservationService reservationService,
                                           UserDataProvider userDataProvider,
                                           ReservationDataProvider reservationDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, userDataProvider,
        reservationDataProvider);
  }

  @Test
  public void returnsCorrectPrice()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotFoundException {
    //given
    User owner = userDataProvider.user("test@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);
    ReservationDto reservationDto = reservationDataProvider.reservationDto(magazine);

    //when
    TotalPrice totalPrice = reservationService.getTotalPrice(reservationDto);

    //then
    BigDecimal expectedPrice = magazine.getPricePerMeter()
        .multiply(reservationDto.getAreaInMeters())
        .multiply(BigDecimal.valueOf(
            DAYS.between(reservationDto.getStartDate(), reservationDto.getEndDate().plusDays(1))));
    BigDecimal expectedServiceFee = expectedPrice.multiply(totalPrice.getServiceFeeMultiplier());
    BigDecimal expectedTax = expectedPrice.multiply(totalPrice.getTaxMultiplier());

    assertEquals(totalPrice.getPrice(), expectedPrice.setScale(2, RoundingMode.HALF_EVEN));
    assertEquals(totalPrice.getServiceFee(),
        expectedServiceFee.setScale(2, RoundingMode.HALF_EVEN));
    assertEquals(totalPrice.getTax(), expectedTax.setScale(2, RoundingMode.HALF_EVEN));
    assertEquals(totalPrice.getTotalPrice(),
        expectedPrice.add(expectedServiceFee).add(expectedTax)
            .setScale(2, RoundingMode.HALF_EVEN));
  }
}
