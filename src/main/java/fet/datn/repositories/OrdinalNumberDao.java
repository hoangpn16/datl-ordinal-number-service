package fet.datn.repositories;

import fet.datn.repositories.entities.OrdinalNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdinalNumberDao extends JpaRepository<OrdinalNumberEntity, Long> {
    OrdinalNumberEntity findOneByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(ordinal_number) FROM `ORDINAL_NUMBERS` WHERE created_time = DATE(CURRENT_DATE)")
    Integer countOrdinalNumber();
}
