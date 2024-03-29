package fet.datn.repositories;

import fet.datn.repositories.entities.OrdinalNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdinalNumberRepository extends JpaRepository<OrdinalNumberEntity, Long> {
    OrdinalNumberEntity findOneByUserId(Long userId);

    OrdinalNumberEntity findOneById(Long id);

    @Query(nativeQuery = true, value = "SELECT COUNT(ordinal_number) FROM `ORDINAL_NUMBERS` WHERE DATE(created_time) = DATE(CURRENT_DATE)")
    Integer countOrdinalNumber();

    @Query(nativeQuery = true, value = "SELECT * FROM `ORDINAL_NUMBERS` WHERE user_id = :userId AND DATE(created_time) = DATE(CURRENT_DATE) AND status IN (0,1)")
    OrdinalNumberEntity findOrdinalNumberByUserIdAndCreateTime(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM `ORDINAL_NUMBERS` WHERE DATE(created_time) = DATE(CURRENT_DATE) AND status = 0 ORDER BY id ASC LIMIT 1")
    OrdinalNumberEntity findNextOrdinalNumber();

    List<OrdinalNumberEntity> findAllByStatusAndCreatedTimeGreaterThan(Integer status, String createdTime);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM `ORDINAL_NUMBERS` WHERE `status` = 0 AND DATE(created_time) = DATE(CURRENT_DATE) AND id < (SELECT `id` FROM ORDINAL_NUMBERS WHERE `user_id` = :userId AND DATE(created_time) = DATE(CURRENT_DATE) AND status =0)")
    Integer countUserIsWaiting(@Param("userId") Long userId);


}
