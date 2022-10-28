package fet.datn.repositories;

import fet.datn.repositories.entities.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    ScheduleEntity findOneById(Long id);

    List<ScheduleEntity> findAllByCustomerId(Long customerId);

    List<ScheduleEntity> findAllByCustomerIdAndStatus(Long customerId, Integer status);
}
