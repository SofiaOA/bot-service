package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChatRepository extends JpaRepository<MemberChat, Integer> {

    Optional<MemberChat> findByMemberId(String id);

}
