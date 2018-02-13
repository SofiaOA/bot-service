package com.hedvig.botService.enteties.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.hedvig.botService.enteties.UserContext;
import org.h2.engine.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Entity
@DiscriminatorValue("singleSelect")
public class MessageBodySingleSelect extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);

	public ArrayList<SelectItem> choices = new ArrayList<SelectItem>();
	
    public MessageBodySingleSelect(String content, List<SelectItem> items) {
    	super(content);
    	this.choices.addAll(items);
	}

    MessageBodySingleSelect(){}

    @JsonIgnore
    public SelectItem getSelectedItem() {
		for (SelectItem o : this.choices) {
			if(o.selected) {
				return o;
			}
		}
		throw new RuntimeException("No item selected.");
	}

	@Override
	public void render(UserContext userContext) {
		choices.forEach(x -> x.render(userContext));

		super.render(userContext);
	}

	public boolean removeItemIf(Predicate<? super SelectItem> predicate) {
		return choices.removeIf(predicate);
	}

}