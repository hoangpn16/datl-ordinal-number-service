package fet.datn.repositories;

import fet.datn.repositories.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenDao extends JpaRepository<TokenEntity, Long> {
    TokenEntity findOneByToken(String token);
}
