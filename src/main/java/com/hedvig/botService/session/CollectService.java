package com.hedvig.botService.session;

import com.hedvig.botService.chat.BankIdChat;
import com.hedvig.botService.enteties.CollectionStatus;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdStatusType;
import com.hedvig.botService.web.dto.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.value;

public class CollectService {

    private final Logger log = LoggerFactory.getLogger(CollectionStatus.class);

    private final UserContextRepository userContextRepository;
    private final MemberService memberService;

    public CollectService(
            UserContextRepository userContextRepository,
            MemberService memberService) {
        this.userContextRepository = userContextRepository;
        this.memberService = memberService;
    }

    public Optional<BankIdAuthResponse> collect(String hid, String referenceToken, BankIdChat chat) {
        UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));


        try {

            CollectionStatus collectionStatus = uc.getBankIdCollectStatus(referenceToken);
            if(collectionStatus == null) {
                log.error("Could not find referenceToken: {}", value("referenceToken", referenceToken));
                chat.bankIdAuthError(uc);

                return Optional.of(
                        new BankIdAuthResponse(
                                BankIdStatusType.COMPLETE,
                                "",
                                "",
                                null));

            } else if(!allowedToCall(collectionStatus)) {
                log.error("Not allowed to call bankId yet, less than 1s passed since last call: {}", value("referenceToken", referenceToken));

                return Optional.of(
                        new BankIdAuthResponse(
                                BankIdStatusType.ERROR,
                                "",
                                collectionStatus.getReferenceToken(),
                                null));
            } else if(collectionStatus.isDone()) {
                log.info("This referenceToken has allready been handled!");
                return Optional.of(
                        new BankIdAuthResponse(
                                BankIdStatusType.COMPLETE,
                                "",
                                referenceToken,
                                null));
            }

            BankIdAuthResponse collect = memberService.collect(referenceToken, hid);
            BankIdStatusType bankIdStatus = collect.getBankIdStatus();
            log.info(
                    "BankIdStatus after collect:{}, memberId:{}, lastCollectionStatus: {}",
                    bankIdStatus.name(),
                    hid,
                    collectionStatus.getLastStatus());

            if(collectionStatus.getCollectionType().equals(CollectionStatus.CollectionType.AUTH)) {

                if(collect.getNewMemberId() != null && !collect.getNewMemberId().equals(hid)){
                    log.info("Found in memberId in response from memberService. Loading other userContext.");
                    uc = userContextRepository.findByMemberId(collect.getNewMemberId()).
                            orElseThrow(() -> new RuntimeException("Could not find usercontext fo new memberId."));

                    collectionStatus.setUserContext(uc);
                }



                if (bankIdStatus == BankIdStatusType.COMPLETE) {
                    //Fetch member data from member service.
                    //Try three times

                    Member member = memberService.getProfile(collect.getNewMemberId());

                    uc.fillMemberData(member);

                    uc.getOnBoardingData().setUserHasAuthWithBankId(referenceToken);

                    chat.bankIdAuthComplete(uc);
                    collectionStatus.setDone();

                }else if (bankIdStatus == BankIdStatusType.ERROR) {
                    //Handle error
                    log.error("Got error response from member service with reference token: {}", value("referenceToken", referenceToken));
                    collectionStatus.addError();

                    if(collectionStatus.shouldAbort()) {
                        chat.bankIdAuthError(uc);
                        collect = createCOMPLETEResponse(collect);
                        collectionStatus.setDone();
                    }
                }




            }else if(collectionStatus.getCollectionType().equals(CollectionStatus.CollectionType.SIGN)) {
                //Do nothing
                if(bankIdStatus == BankIdStatusType.COMPLETE) {
                    chat.memberSigned(referenceToken, uc);
                    collectionStatus.setDone();
                }
                else if(bankIdStatus == BankIdStatusType.ERROR) {
                    log.error("Got error response from member service with reference token: {}", value("referenceToken", referenceToken));
                    collectionStatus.addError();
                    if(collectionStatus.shouldAbort()) {
                        chat.bankIdSignError(uc);
                        collect = createCOMPLETEResponse(collect);
                        collectionStatus.setDone();
                    }
                }
            }

            collectionStatus.update(bankIdStatus);
            userContextRepository.saveAndFlush(uc);

            return Optional.of(collect);
        }catch( HttpClientErrorException ex) {
            log.error("Error collecting result from member-service: ", ex);
            chat.bankIdAuthError(uc);
            //Have hedvig respond with error
        }catch( ObjectOptimisticLockingFailureException ex) {
            log.error("Could not save user context: ", ex);
            return Optional.of(new BankIdAuthResponse(BankIdStatusType.ERROR, "", "", ""));
        }

        return Optional.empty();
    }

    private BankIdAuthResponse createCOMPLETEResponse(BankIdAuthResponse collect) {
        return new BankIdAuthResponse(
                BankIdStatusType.COMPLETE,
                "",
                collect.getReferenceToken(),
                collect.getNewMemberId());
    }

    private boolean allowedToCall(CollectionStatus collectionStatus) {
        Instant now = Instant.now();
        //log.debug("Last call time: {}, currentTime: {}", getLastCallTime(), now);
        return Duration.between(collectionStatus.getLastCallTime(), now).toMillis() > 1000;
    }
}
