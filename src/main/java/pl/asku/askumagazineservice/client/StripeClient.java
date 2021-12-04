package pl.asku.askumagazineservice.client;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import java.math.BigDecimal;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeClient {

  @Value("${stripe.api-key}")
  private String apiKey;

  @Autowired
  StripeClient() {
    Stripe.apiKey = apiKey;
  }

  public PaymentIntent charge(BigDecimal amount) throws StripeException {
    Stripe.apiKey = apiKey;

    PaymentIntentCreateParams params =
            PaymentIntentCreateParams.builder()
                    .setAmount(amount.longValue())
                    .setCurrency("usd")
                    .build();

    return PaymentIntent.create(params);
  }
}
