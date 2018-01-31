package com.hedvig.botService.enteties.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("multipleChoice")
public class MessageBodyMultipleSelect extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);
	
	public ArrayList<SelectItem> choices = new ArrayList<SelectItem>();
	
    public MessageBodyMultipleSelect(String content, ArrayList<SelectItem> items) {
    	super(content);
		this.choices.addAll(items); // TODO
	}
    MessageBodyMultipleSelect(){}

	public String selectedOptionsAsString() {
    	StringBuilder accumulator = new StringBuilder();
		final List<SelectItem> selectedOptions = this.choices.stream().filter(x -> x.selected).collect(Collectors.toList());


		final int nrSelectedOptions = selectedOptions.size();
		if(nrSelectedOptions == 1) {
			return selectedOptions.get(0).text.toLowerCase();
		}

		for(int i = 0; i < nrSelectedOptions; i++) {
			final SelectItem selectItem = selectedOptions.get(i);
			if(selectItem instanceof  SelectOption && ((SelectOption)selectItem).clearable) {
				continue;
			}

			int optionsLeft = (nrSelectedOptions - (i+1));
			if( accumulator.length() > 0 && optionsLeft > 0){
				accumulator.append(", ");
			}else if ( accumulator.length() > 0 && optionsLeft == 0) {
				accumulator.append(" och ");
			}

			accumulator.append(selectItem.text.toLowerCase());

		}
		return accumulator.toString();
	}

	public List<SelectOption> selectedOptions() {
		return this.choices.stream().
				filter(x -> x.selected).
				filter(SelectOption.class::isInstance).
				map(SelectOption.class::cast).
				collect(Collectors.toList());
	}

	public long getNoSelectedOptions() {
    	return this.choices.stream().filter(x -> x.selected).count();
	}
}