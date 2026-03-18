package in.onlinebiodatamaker.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import in.onlinebiodatamaker.model.AppUsageLog;
import in.onlinebiodatamaker.model.Payment;
import in.onlinebiodatamaker.repo.AppUsageLogRepository;
import in.onlinebiodatamaker.repo.PaymentRepository;
import in.onlinebiodatamaker.service.PricingService;
import in.onlinebiodatamaker.util.LogUtil;
import in.onlinebiodatamaker.util.PaymentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
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

    @Value("${razorpay.currency}")
    private String currency;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private PaymentUtil paymentUtil;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppUsageLogRepository appUsageLogRepository;

    @PostMapping("/webhook")
    public String webhook(@RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        try {

            boolean valid = Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    webhookSecret
            );

            if (valid) {
                System.out.println("Valid webhook received");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ok";
    }

    @PostMapping("/create-order")
    @ResponseBody
    public Map<String, Object> createOrder(@RequestParam(required = false) String coupon, HttpServletRequest request, HttpSession session) throws Exception {

        int finalAmount = pricingService.getFinalAmount(coupon);

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", finalAmount * 100); // Razorpay expects paise
        options.put("currency", currency);
        options.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = client.orders.create(options);

        String orderId = order.get("id");

        // ⭐ Store orderId in session
        session.setAttribute("ORDER_ID", orderId);

        // 🔹 Save payment record with CREATED status
        Payment payment = new Payment();
        payment.setRazorpayOrderId(order.get("id"));
        payment.setAmount(finalAmount);
        payment.setCurrency(currency);
        payment.setStatus("CREATED");
        payment.setCouponCode(coupon);
        payment.setCreatedAt(java.time.LocalDateTime.now());
        String ip = paymentUtil.getClientIp(request);
        payment.setUserIp(ip);
        paymentRepository.save(payment);

        String userAgent = request.getHeader("User-Agent");

        AppUsageLog log = new AppUsageLog();

        log.setIpAddress(ip);
        log.setCountry(logUtil.getCountry(ip));
        log.setSessionId(request.getSession().getId());
        log.setUserAgent(userAgent);
        log.setDeviceType(logUtil.getDeviceType(userAgent));

        log.setAction("payment_attempt");

        appUsageLogRepository.save(log);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));

        return response;
    }

    @PostMapping("/verify-payment")
    @ResponseBody
    public String verifyPayment(@RequestBody Map<String, String> data,
            HttpSession session,
            HttpServletRequest request) throws Exception {

        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        // ⭐ Validate order belongs to this session
        String sessionOrderId = (String) session.getAttribute("ORDER_ID");

        if (sessionOrderId == null || !sessionOrderId.equals(orderId)) {
            return "invalid-order";
        }

        boolean isValid = Utils.verifySignature(
                orderId + "|" + paymentId,
                signature,
                keySecret
        );

        Payment payment = paymentRepository
                .findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        // ⭐ Capture request info once
        String ip = paymentUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        payment.setUserIp(ip);

        // ⭐ Prepare usage log
        AppUsageLog log = new AppUsageLog();
        log.setIpAddress(ip);
        log.setCountry(logUtil.getCountry(ip));
        log.setSessionId(session.getId());
        log.setUserAgent(userAgent);
        log.setDeviceType(logUtil.getDeviceType(userAgent));

        if (isValid) {

            payment.setRazorpayPaymentId(paymentId);
            payment.setRazorpaySignature(signature);
            payment.setStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());

            log.setAction("payment_success");

            session.setAttribute("PAID", true);

        } else {

            payment.setStatus("FAILED");

            log.setAction("payment_failed");
        }

        // ⭐ Save once
        paymentRepository.save(payment);
        appUsageLogRepository.save(log);

        return isValid ? "success" : "fail";
    }
}
