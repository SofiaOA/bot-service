package com.hedvig.botService.enteties;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface UserContextRepository extends JpaRepository<UserContext, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<UserContext> findByMemberId(String id);
}
