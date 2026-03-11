package in.onlinebiodatamaker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Razorpay IDs
    @Column(unique = true)
    private String razorpayOrderId;
    @Column(unique = true)
    private String razorpayPaymentId;
    private String razorpaySignature;

    // Payment info
    private Integer amount;
    private String currency;

    // Status
    private String status; // CREATED, SUCCESS, FAILED

    // Coupon used
    private String couponCode;

    // User info
    private String userEmail;
    private String userIp;

    // timestamps
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
