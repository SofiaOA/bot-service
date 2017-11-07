package com.hedvig.botService.chat;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hedvig.botService.chat.Conversation.EventTypes;
import com.hedvig.botService.dataTypes.HedvigDataType;
import com.hedvig.botService.enteties.*;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBody;
import com.hedvig.botService.enteties.message.MessageBodyBankIdCollect;
import com.hedvig.botService.enteties.message.MessageBodyMultipleSelect;
import com.hedvig.botService.enteties.message.MessageBodyNumber;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.MessageHeader;
import com.hedvig.botService.enteties.message.SelectItem;
import com.hedvig.botService.enteties.message.SelectLink;
import com.hedvig.botService.enteties.message.SelectOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Conversation {

        public static final long  HEDVIG_USER_ID = 1; // The id hedvig uses to chat
        private Map<String, SelectItemMessageCallback> callbacks = new TreeMap<>();
        public static enum conversationStatus {INITIATED, ONGOING, COMPLETE}
        public static enum EventTypes {ANIMATION_COMPLETE, MODAL_CLOSED, MESSAGE_FETCHED};
        
	private static final String regexPattern = "\\{(.*?)\\}";
	private static Logger log = LoggerFactory.getLogger(Conversation.class);
	private String conversationName; // Id for the conversation

	//MemberChat memberChat;
	//UserContext userContext;
	//SessionManager sessionManager;
	private TreeMap<String, Message> messageList = new TreeMap<String, Message>();
	//HashMap<String, String> conversationContext = new HashMap<String, String>(); // Context specific information learned during conversation
	
	Conversation(String conversationId) {
		this.conversationName = conversationId;
		//this.memberChat = mc;
		//this.userContext = uc;
		//this.sessionManager = session;
	}

	public Message getMessage(String key){
		Message m = messageList.get(key);
		if(m==null)log.info("Message not found with id:" + key);
		return m;
	}
	
	public void storeMessage(String key, Message m){
		messageList.put(key, m);
	}
	
	public String getConversationName() {
		return conversationName;
	}

	private String replaceWithContext(UserContext userContext, String input){
		log.info("Contextualizing string:" + input);
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher m = pattern.matcher(input);
		while (m.find()) {
			String s = m.group();
			String r = userContext.getDataEntry(s);
			log.debug(s + ":" + r);
			if(r!=null){input = input.replace(s, r);}
		}
		log.debug("-->" + input);
		return input;
	}
	
	void addToChat(Message m, UserContext userContext, MemberChat memberChat) {
		log.info("Putting message:" + m.id + " content:" + m.body.text);
		m.body.text = replaceWithContext(userContext, m.body.text);
		if(m.body.getClass() == MessageBodySingleSelect.class) {
		    MessageBodySingleSelect mss = (MessageBodySingleSelect) m.body;
            mss.choices.forEach(x -> {
                if(x.getClass() == SelectLink.class) {
                    SelectLink link = (SelectLink) x;
                    if(link.appUrl != null) {
						link.appUrl = replaceWithContext(userContext, link.appUrl);
					}
					if(link.webUrl != null) {
                    	link.webUrl = replaceWithContext(userContext, link.webUrl);
					}
                }
            });
		}else if(m.body.getClass() == MessageBodyBankIdCollect.class) {
		    MessageBodyBankIdCollect mbc = (MessageBodyBankIdCollect) m.body;
		    mbc.referenceId = replaceWithContext(userContext, mbc.referenceId);
        }
		memberChat.addToHistory(m);
	}

	private void createMessage(String id, MessageHeader header, MessageBody body){
		Message m = new Message();
		m.id = id;
		m.header = header;
		m.body = body;
		messageList.put(m.id, m);
	}

	private void createMessage(String id, MessageHeader header, MessageBody body, Integer delay){
		Message m = new Message();
		m.id = id;
		m.header = header;
		m.body = body;
		m.header.pollingInterval = new Long(delay);
		messageList.put(m.id, m);
	}


	void createMessage(String id, MessageBody body, SelectItemMessageCallback callback) {
		this.createMessage(id, body);
		this.setMessageCallback(id, callback);
	}

	protected void setMessageCallback(String id, SelectItemMessageCallback callback) {
		this.callbacks.put(id, callback);
	}

	boolean hasSelectItemCallback(String messageId) {
	    return this.callbacks.containsKey(messageId);
    }

    String execSelectItemCallback(String messageId, UserContext uc, SelectItem item) {
	    return this.callbacks.get(messageId).operation(uc, item);
    }

    // -------------------------
    
    void createMessage(String id, MessageBody body, Integer delay){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		createMessage(id,header,body,delay);    	
    }
    
	void createMessage(String id, MessageBody body, String avatarName, Integer delay){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		header.avatarName = avatarName;
		createMessage(id,header,body,delay);		
	}
	
	void createMessage(String id, MessageBody body, String avatarName, SelectItemMessageCallback callback){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		header.avatarName = avatarName;
		this.setMessageCallback(id, callback);
		createMessage(id,header,body);		
	}
	
	void createMessage(String id, MessageBody body, Image image, Integer delay){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		body.imageURL = image.imageURL;
		body.imageHeight = image.imageHeight;
		body.imageWidth = image.imageWidth;
		createMessage(id,header,body,delay);			
	}
	 
	// -------------------------
	
	void createMessage(String id, MessageBody body){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		createMessage(id,header,body);
	}
	
	void createMessage(String id, MessageBody body, String avatarName){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		header.avatarName = avatarName;
		createMessage(id,header,body);		
	}
	
	void createMessage(String id, MessageBody body, Image image){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		body.imageURL = image.imageURL;
		body.imageHeight = image.imageHeight;
		body.imageWidth = image.imageWidth;
		createMessage(id,header,body);			
	}

	void startConversation(UserContext userContext, MemberChat memberChat, String startId){
		log.info("Starting conversation with message:" + startId);
		addToChat(messageList.get(startId), userContext, memberChat);
	}
	
    public int getValue(MessageBodyNumber body){
    	return Integer.parseInt(body.text);
    }
    
    public String getValue(MessageBodySingleSelect body){

		for(SelectItem o : body.choices){
			if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
				return SelectOption.class.cast(o).value;
			}
		}   	
		return "";
    }
    
    public ArrayList<String> getValue(MessageBodyMultipleSelect body){
		ArrayList<String> selectedOptions = new ArrayList<String>();
		for(SelectItem o : body.choices){
			if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
				 selectedOptions.add(SelectOption.class.cast(o).value);
			}
		}   
		return selectedOptions;
    }

    public void setExpectedReturnType(String messageId, HedvigDataType type){
    	if(getMessage(messageId)!=null){
    		log.debug("Setting the expected return typ for message:" + messageId + " to " + type.getClass().getName());
    		getMessage(messageId).expectedType = type;
    	}else{
    		log.error("ERROR: ------------> Message not found:" + messageId);
    	}
    }
    
    // If the message has a preferred return type it is validated otherwise not
    public boolean validateReturnType(Message m, UserContext userContext, MemberChat memberChat){
    	
    	Message mCorr = getMessage(m.id);
    	
    	if(mCorr != null && mCorr.expectedType!=null){
    		boolean ok = mCorr.expectedType.validate(m.body.text);
    		if(!ok)mCorr.body.text = mCorr.expectedType.getErrorMessage();
    		addToChat(m, userContext, memberChat);
    		addToChat(mCorr, userContext, memberChat);
    		return ok;
    	}
    	return true;
    }
    
    // ------------------------------------------------------------------------------- //

	public abstract void recieveMessage(UserContext userContext, MemberChat memberChat, Message m);
	public void completeRequest(String nxtMsg, UserContext userContext, MemberChat memberChat) {
		if(getMessage(nxtMsg)!=null)addToChat(getMessage(nxtMsg), userContext, memberChat);	
	}

	public void recieveEvent(EventTypes e, String value, UserContext userContext, MemberChat memberChat) {}

	public void init(UserContext userContext, MemberChat memberChat) {
		// TODO Auto-generated method stub
		
	}

}
