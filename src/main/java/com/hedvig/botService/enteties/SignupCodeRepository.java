package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignupCodeRepository extends JpaRepository<SignupCode, Integer> {

    Optional<SignupCode> findByEmail(String email);
    Long countByEmail(String email);

}
