package in.onlinebiodatamaker.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import in.onlinebiodatamaker.service.PricingService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.amount}")
    private int amount;

    @Value("${razorpay.currency}")
    private String currency;

    @Autowired
    private PricingService pricingService;

    @PostMapping("/create-order")
    @ResponseBody
    public Map<String, Object> createOrder(@RequestParam(required = false) String coupon) throws Exception {

        int finalAmount = pricingService.getFinalAmount(coupon);
        System.out.println("KEY ID = " + keyId);
        System.out.println("KEY SECRET = " + keySecret);
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", finalAmount);
        options.put("currency", currency);
        options.put("receipt", "txn_123456");

        Order order = client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));

        return response;
    }

    @PostMapping("/verify-payment")
    @ResponseBody
    public String verifyPayment(@RequestBody Map<String, String> data,
            HttpSession session) throws Exception {

        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        boolean isValid = Utils.verifySignature(
                orderId + "|" + paymentId,
                signature,
                keySecret
        );

        if (isValid) {
            session.setAttribute("PAID", true);   // 🔥 mark as paid
            return "success";
        } else {
            return "fail";
        }
    }
}
