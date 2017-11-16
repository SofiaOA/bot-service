package com.hedvig.botService.serviceIntegration;

import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.web.dto.Member;
import org.springframework.stereotype.Component;

@Component
public class FakeMemberCreator {


    private MemberService memberService;
    private ProductPricingService productPricingService;

    public FakeMemberCreator(MemberService memberService, ProductPricingService productPricingService) {
        this.memberService = memberService;
        this.productPricingService = productPricingService;
    }

    public void doCreate(UserContext uc){
        String memberId = uc.getMemberId();
        Member member = memberService.convertToFakeUser(memberId);
        uc.fillMemberData(member);
        UserData data = uc.getOnBoardingData();
        data.setUserHasAuthWithBankId("afdsaf");
        data.setHouseType("ägerbostdsrätt");
        data.setCurrentInsurer("trygg-hansa");
        data.setPersonInHouseHold(3);
        data.setLivingSpace(123.0f);
        data.addSecurityItem("safety.extinguisher");
        productPricingService.createProduct(memberId, uc.getOnBoardingData());
        productPricingService.quoteAccepted(memberId);
        memberService.finalizeOnBoarding(memberId, data);
        productPricingService.contractSigned(memberId, "referenceToken");
    }


}
