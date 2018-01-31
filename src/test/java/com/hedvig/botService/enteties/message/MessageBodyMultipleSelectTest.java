package com.hedvig.botService.enteties.message;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class MessageBodyMultipleSelectTest {


    @Test
    public void selectedOptionsAsString(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("text1", "value", true));

        assertThat(sut.selectedOptionsAsString(), equalTo("text1"));
    }

    @Test
    public void selectedOptionsAsStringUsesAndForTwoSelectedOptions(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("text1", "value1", true));
        sut.choices.add(new SelectOption("text2", "value2", true));

        assertThat(sut.selectedOptionsAsString(), equalTo("text1 och text2"));
    }

    @Test
    public void selectedOptionsAsStringUsesAndForFiveSelectedOptions(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        for(int i = 0; i < 5; i++) {
            sut.choices.add(new SelectOption(
                    "text" + Objects.toString(i),
                    "value" + Objects.toString(i),
                    true));
        }
        assertThat(sut.selectedOptionsAsString(), equalTo("text0, text1, text2, text3 och text4"));
    }

    @Test
    public void selectedOptionsAsStringUsesAndForNoOptionsSelected(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("text1", "value1", false));
        sut.choices.add(new SelectOption("text2", "value2", false));

        assertThat(sut.selectedOptionsAsString(), equalTo(""));
    }

    @Test
    public void selectedOptionsAsStringReturnsTextAsLowerCase(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("Text1", "value1", true));
        sut.choices.add(new SelectOption("Text2", "value2", true));

        assertThat(sut.selectedOptionsAsString(), equalTo("text1 och text2"));
    }

    @Test
    public void selectedOptionsAsStringReturnsTextAsLowerCaseWithClearable(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("Text1", "value1", true));
        sut.choices.add(new SelectOption("Text2", "value2", true, true));

        assertThat(sut.selectedOptionsAsString(), equalTo("text1"));
    }

    @Test
    public void selectedOptionsAsString_With_OneClearable(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("Text2", "value2", true, true));

        assertThat(sut.selectedOptionsAsString(), equalTo("text2"));
    }

    @Test
    public void selectedOptionsAsString_With_ClearableInMiddle(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        sut.choices.add(new SelectOption("Text1", "value1", true));
        sut.choices.add(new SelectOption("Text2", "value2", true, true));
        sut.choices.add(new SelectOption("Text3", "value1", true));

        assertThat(sut.selectedOptionsAsString(), equalTo("text1 och text3"));
    }


    @Test public void getSelectedOptions(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        final SelectOption option1 = new SelectOption("text1", "value1", true);
        final SelectOption option2 = new SelectOption("text2", "value2", true);
        sut.choices.add(option1);
        sut.choices.add(option2);

        assertThat(sut.selectedOptions(), contains(option1, option2));

    }

    @Test public void getSelectedOptionsWithNoSelectedOption(){
        MessageBodyMultipleSelect sut = new MessageBodyMultipleSelect();
        final SelectOption option1 = new SelectOption("text1", "value1", false);
        final SelectOption option2 = new SelectOption("text2", "value2", false);
        sut.choices.add(option1);
        sut.choices.add(option2);

        assertThat(sut.selectedOptions(), empty());
    }

}