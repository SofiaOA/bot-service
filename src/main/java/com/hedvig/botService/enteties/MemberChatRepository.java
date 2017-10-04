package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.OnboardingConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChatRepository extends JpaRepository<MemberChat, Integer> {

    Optional<MemberChat> findByMemberId(String id);

}
