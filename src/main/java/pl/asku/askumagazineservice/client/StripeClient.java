package pl.asku.askumagazineservice.client;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeClient {

  @Value("${stripe.api-key}")
  private String apiKey;

  @Autowired
  StripeClient() {
    System.out.println(apiKey);
    Stripe.apiKey = apiKey;
  }

  public Customer createCustomer(String token, String email) throws StripeException {
    Map<String, Object> customerParams = new HashMap<>();
    customerParams.put("email", email);
    customerParams.put("source", token);
    return Customer.create(customerParams);
  }

  private Customer getCustomer(String id) throws StripeException {
    return Customer.retrieve(id);
  }

  public Charge chargeNewCard(String token, BigDecimal amount) throws StripeException {
    System.out.println(apiKey);
    Stripe.apiKey = apiKey;
    Map<String, Object> chargeParams = new HashMap<>();
    chargeParams.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
    chargeParams.put("currency", "USD");
    chargeParams.put("source", token);

    return Charge.create(chargeParams);
  }

  public Charge chargeCustomerCard(String customerId, BigDecimal amount) throws StripeException {
    String sourceCard = getCustomer(customerId).getDefaultSource();
    Map<String, Object> chargeParams = new HashMap<>();
    chargeParams.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
    chargeParams.put("currency", "USD");
    chargeParams.put("customer", customerId);
    chargeParams.put("source", sourceCard);
    return Charge.create(chargeParams);
  }
}
