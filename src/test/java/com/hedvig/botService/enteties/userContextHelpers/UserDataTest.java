import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UserDataTest {

    @Test
    public void testsetAddressCity(){
        UserContext context = new UserContext();

        UserData ud = context.getOnBoardingData();

        ud.setAddressCity("Sollefteå");

        assertThat(ud.getAddressCity()).isEqualTo("Sollefteå");
    }

}