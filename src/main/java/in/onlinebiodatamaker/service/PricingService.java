package in.onlinebiodatamaker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class PricingService {

    @Value("${biodata.price.base}")
    private int basePrice;

    @Value("${biodata.price.couponDiscount}")
    private int discount;

    @Value("${biodata.price.couponCode}")
    private String validCoupon;

    public int getFinalAmount(String couponCode) {
        if (couponCode != null && couponCode.equalsIgnoreCase(validCoupon)) {
            return basePrice - discount;
        }
        return basePrice;
    }

    public boolean isValidCoupon(String couponCode) {
        return couponCode != null && couponCode.equalsIgnoreCase(validCoupon);
    }
}
