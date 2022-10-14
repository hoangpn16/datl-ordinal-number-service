package fet.datn.repositories;


import fet.datn.repositories.entities.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.sql.Timestamp;
import java.util.List;

public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {

    List<OtpEntity> findAllByOtpReferenceId(String otpReferenceId);
    List<OtpEntity> findAllByOtpReferenceIdAndIsDeletedIsFalse(String otpReferenceId);

    @Query("SELECT COUNT(distinct o.otpReferenceId) FROM OtpEntity o WHERE o.mobileNumber = :mobile_number AND o.type = :type  AND o.createdTimestamp >= :time")
    Long countGeneratedOtpsByMobileNumber(@Param("mobile_number") String mobile_number, @Param("type") String type, @Param("time")Timestamp time);
}