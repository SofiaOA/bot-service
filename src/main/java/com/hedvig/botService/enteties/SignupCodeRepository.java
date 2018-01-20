package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SignupCodeRepository extends JpaRepository<SignupCode, Integer> {

    Optional<SignupCode> findByEmail(String email);
    Optional<SignupCode> findByCode(String code);
    Long countByEmail(String email);
    List<SignupCode> findAllByOrderByDateAsc();
}
