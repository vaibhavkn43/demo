package in.onlinebiodatamaker.repo;

import in.onlinebiodatamaker.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author admin
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>  {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    boolean existsByStatusAndUserIp(String status, String userIp);
}
