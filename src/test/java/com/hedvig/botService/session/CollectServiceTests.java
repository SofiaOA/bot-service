package com.hedvig.botService.session;

import com.hedvig.botService.chat.BankIdChat;
import com.hedvig.botService.enteties.CollectionStatus;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdStatusType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CollectServiceTests {

    @MockBean
    MemberService memberService;

    @MockBean
    UserContextRepository userContextRepository;

    @MockBean
    BankIdChat chat;

    @After
    public void reset_mocks() {
        Mockito.reset(userContextRepository);
        Mockito.reset(memberService);
        Mockito.reset(chat);
    }

    @Test
    public void testCollectError() {

        final String memberId = "1234";
        final String referenceToken = "referenceToken";


        CollectionStatus collectionStatus = new CollectionStatus();
        collectionStatus.setLastCallTime(Instant.now());
        collectionStatus.setCollectionType(CollectionStatus.CollectionType.AUTH);

        UserContext userContext = new UserContext();
        userContext.setBankIdStatus(new HashMap<String, CollectionStatus>(){{
          put(referenceToken, collectionStatus);
        }});

        when(userContextRepository.findByMemberId(memberId)).thenReturn(Optional.of(userContext));

        BankIdAuthResponse response = new BankIdAuthResponse(BankIdStatusType.ERROR, "", referenceToken, null);
        when(memberService.collect(referenceToken, memberId)).thenReturn(response);


        CollectService service = new CollectService(userContextRepository, memberService);

        BankIdAuthResponse expected = new BankIdAuthResponse(BankIdStatusType.ERROR, "", null, null);

        assertThat(service.collect(memberId, referenceToken, null)).isEqualTo(Optional.of(expected));

    }


    @Test
    public void responseStatusIsCOMPLETE_AfterMany_Errors() {

        final String autoStartToken = "autoStart";
        final String memberId = "1234";
        final String referenceToken = "referenceToken";


        CollectionStatus collectionStatus = new CollectionStatus();
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        collectionStatus.setCollectionType(CollectionStatus.CollectionType.AUTH);
        collectionStatus.setLastStatus("OUTSTANDING_TRANSACTION");

        UserContext userContext = new UserContext();
        userContext.setBankIdStatus(new HashMap<String, CollectionStatus>(){{
            put(referenceToken, collectionStatus);
        }});

        when(userContextRepository.findByMemberId(memberId)).thenReturn(Optional.of(userContext));


        BankIdAuthResponse response = new BankIdAuthResponse(BankIdStatusType.ERROR, autoStartToken, referenceToken, null);
        when(memberService.collect(referenceToken, memberId)).thenReturn(response);

        CollectService service = new CollectService(userContextRepository, memberService);

        BankIdAuthResponse expectedError = new BankIdAuthResponse(BankIdStatusType.ERROR, autoStartToken, referenceToken, null);
        BankIdAuthResponse expectedComplete = new BankIdAuthResponse(BankIdStatusType.COMPLETE, "", referenceToken, null);

        //ACT

        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedError));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedError));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedError));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedComplete));

        //VERIFY

        verify(chat, times(1)).bankIdAuthError(userContext);

    }

    @Test
    public void responseStatusIsCOMPLETE_AfterMany_SignComplete() {

        final String autoStartToken = "autoStart";
        final String memberId = "1234";
        final String referenceToken = "referenceToken";


        CollectionStatus collectionStatus = new CollectionStatus();
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        collectionStatus.setCollectionType(CollectionStatus.CollectionType.SIGN);
        collectionStatus.setLastStatus("OUTSTANDING_TRANSACTION");

        UserContext userContext = new UserContext();
        userContext.setBankIdStatus(new HashMap<String, CollectionStatus>(){{
            put(referenceToken, collectionStatus);
        }});

        when(userContextRepository.findByMemberId(memberId)).thenReturn(Optional.of(userContext));


        BankIdAuthResponse response = new BankIdAuthResponse(BankIdStatusType.COMPLETE, autoStartToken, referenceToken, null);
        when(memberService.collect(referenceToken, memberId)).thenReturn(response);

        CollectService service = new CollectService(userContextRepository, memberService);

        BankIdAuthResponse expected = new BankIdAuthResponse(BankIdStatusType.COMPLETE, autoStartToken, referenceToken, null);

        //ACT

        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expected));

        //VERIFY

        verify(chat, times(1)).memberSigned(referenceToken, userContext);

    }


    @Test
    public void responseStatusIsCOMPLETE_AfterMany_SignError() {

        final String autoStartToken = "autoStart";
        final String memberId = "1234";
        final String referenceToken = "referenceToken";


        CollectionStatus collectionStatus = new CollectionStatus();
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        collectionStatus.setCollectionType(CollectionStatus.CollectionType.SIGN);
        collectionStatus.setLastStatus("OUTSTANDING_TRANSACTION");

        UserContext userContext = new UserContext();
        userContext.setBankIdStatus(new HashMap<String, CollectionStatus>(){{
            put(referenceToken, collectionStatus);
        }});

        when(userContextRepository.findByMemberId(memberId)).thenReturn(Optional.of(userContext));


        BankIdAuthResponse response = new BankIdAuthResponse(BankIdStatusType.ERROR, autoStartToken, referenceToken, null);
        when(memberService.collect(referenceToken, memberId)).thenReturn(response);

        CollectService service = new CollectService(userContextRepository, memberService);

        BankIdAuthResponse expectedError = new BankIdAuthResponse(BankIdStatusType.ERROR, autoStartToken, referenceToken, null);
        BankIdAuthResponse expectedComplete = new BankIdAuthResponse(BankIdStatusType.COMPLETE, "", referenceToken, null);

        //ACT

        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedError));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedError));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedError));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedComplete));
        collectionStatus.setLastCallTime(Instant.now().minusSeconds(2));
        assertThat(service.collect(memberId, referenceToken, chat)).isEqualTo(Optional.of(expectedComplete));

        //VERIFY

        verify(chat, times(1)).bankIdSignError(userContext);

    }

}
