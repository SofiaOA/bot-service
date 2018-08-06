package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface UserContextRepository extends JpaRepository<UserContext, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<UserContext> findByMemberId(String id);
}
