package in.onlinebiodatamaker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import in.onlinebiodatamaker.model.AppUsageLog;
/**
 *
 * @author Vaibhav
 */
public interface AppUsageLogRepository extends JpaRepository<AppUsageLog, Long> {
    
}
