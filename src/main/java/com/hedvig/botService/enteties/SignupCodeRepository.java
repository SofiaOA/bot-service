package com.hedvig.botService.enteties;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupCodeRepository extends JpaRepository<SignupCode, Integer> {

  Optional<SignupCode> findByEmail(String email);

  Optional<SignupCode> findByCode(String code);

  Long countByEmail(String email);

  List<SignupCode> findAllByOrderByDateAsc();
}
