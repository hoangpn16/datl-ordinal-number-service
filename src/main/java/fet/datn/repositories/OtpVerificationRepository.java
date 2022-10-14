package fet.datn.repositories;

import fet.datn.repositories.entities.OtpVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OtpVerificationRepository extends JpaRepository<OtpVerificationEntity, String> {
}
