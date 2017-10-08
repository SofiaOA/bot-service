package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserContextRepository extends JpaRepository<UserContext, Integer> {

    Optional<UserContext> findByMemberId(String id);

}
