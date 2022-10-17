package fet.datn.repositories;

import fet.datn.repositories.entities.EmployeesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeesRepository extends JpaRepository<EmployeesEntity, Long> {
    EmployeesEntity findOneByUserName(String accountName);

    EmployeesEntity findOneByUserId(Long id);
}
