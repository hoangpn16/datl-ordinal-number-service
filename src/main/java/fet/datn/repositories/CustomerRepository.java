package fet.datn.repositories;

import fet.datn.repositories.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    CustomerEntity findOneByUserId(Long userId);

    CustomerEntity findOneByPhone(String phone);
}
