package com.hedvig.botService.session;

import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.web.dto.BankidStartResponse;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OnboardingService {

    final private MemberService memberService;
    final private UserContextRepository userContextRepository;

    public OnboardingService(MemberService memberService, UserContextRepository userContextRepository) {
        this.memberService = memberService;
        this.userContextRepository = userContextRepository;
    }

    public BankidStartResponse sign(String hid) {

        UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

        UserData ud = uc.getOnBoardingData();

        String signText;
        if(ud.getCurrentInsurer() != null) {
            signText = "Jag har tagit del av förköpsinformation och villkor och bekräftar genom att signera att jag vill byta till Hedvig när min gamla försäkring går ut. Jag ger också  Hedvig fullmakt att byta försäkringen åt mig.";
        } else {
            signText = "Jag har tagit del av förköpsinformation och villkor och bekräftar genom att signera att jag skaffar en försäkring hos Hedvig.";
        }

        val signData = memberService.signEx(ud.getSSN(), signText, hid);

        return new BankidStartResponse(signData.getAutoStartToken(), signData.getReferenceToken());
    }
}
