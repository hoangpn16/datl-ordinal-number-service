package fet.datn.repositories;

import fet.datn.repositories.entities.OtpRestrictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OtpRestrictionRepository extends JpaRepository<OtpRestrictionEntity, Integer> {

    @Query("SELECT o FROM OtpRestrictionEntity o WHERE o.userId = :user_id AND o.createdTimestamp >= :time")
    List<OtpRestrictionEntity> findOtpRestrictionByUserIdAndCreateTimeLessThan(@Param("user_id") Integer userId, @Param("time")Timestamp time);

    @Query("SELECT o FROM OtpRestrictionEntity o WHERE o.mobileNumber = :mobile_number AND o.createdTimestamp >= :time")
    List<OtpRestrictionEntity> findOtpRestrictionByMobileAndCreateTimeLessThan(@Param("mobile_number") String mobileNumber, @Param("time")Timestamp time);
}
