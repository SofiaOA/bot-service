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

		for(int i = 0; i < selectedOptions.size(); i++) {
    		accumulator.append(selectedOptions.get(i).text.toLowerCase());
    		int optionsLeft = (selectedOptions.size() - (i+1));
    		if(optionsLeft > 1) {
    			accumulator.append(", ");
			}else if(optionsLeft > 0) {
    			accumulator.append(" och ");
			}
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