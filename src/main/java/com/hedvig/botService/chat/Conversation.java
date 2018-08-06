package com.hedvig.botService.chat;

import com.hedvig.botService.dataTypes.HedvigDataType;
import com.hedvig.botService.dataTypes.TextInput;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Conversation {

  public static final long HEDVIG_USER_ID = 1; // The id hedvig uses to chat
  private Map<String, SelectItemMessageCallback> callbacks = new TreeMap<>();

  public enum conversationStatus {
    INITIATED,
    ONGOING,
    COMPLETE
  }

  public enum EventTypes {
    ANIMATION_COMPLETE,
    MODAL_CLOSED,
    MESSAGE_FETCHED,
    MISSING_DATA
  };

  private static Logger log = LoggerFactory.getLogger(Conversation.class);

  private TreeMap<String, Message> messageList = new TreeMap<>();
  private TreeMap<String, String> relayList = new TreeMap<>();

  Conversation() {}

  public Message getMessage(String key) {
    Message m = messageList.get(key);
    if (m == null) log.info("Message not found with id: " + key);
    return m;
  }

  void addRelay(String s1, String s2) {
    relayList.put(s1, s2);
  }

  String getRelay(String s1) {
    return relayList.get(s1);
  }

  void addToChat(String messageId, UserContext userContext) {
    addToChat(getMessage(messageId), userContext);
  }

  public abstract List<SelectItem> getSelectItemsForAnswer(UserContext uc);

  public abstract boolean canAcceptAnswerToQuestion(UserContext uc);

  void addToChat(Message m, UserContext userContext) {
    m.render(userContext);
    log.info("Putting message: " + m.id + " content: " + m.body.text);
    userContext.addToHistory(m);
  }

  protected void createMessage(String id, MessageHeader header, MessageBody body) {
    Message m = new Message();
    m.id = id;
    m.header = header;
    m.body = body;
    messageList.put(m.id, m);
  }

  private void createMessage(String id, MessageHeader header, MessageBody body, Integer delay) {
    Message m = new Message();
    m.id = id;
    m.header = header;
    m.body = body;
    m.header.pollingInterval = new Long(delay);
    messageList.put(m.id, m);
  }

  protected void setMessageCallback(String id, SelectItemMessageCallback callback) {
    this.callbacks.put(id, callback);
  }

  boolean hasSelectItemCallback(String messageId) {
    return this.callbacks.containsKey(messageId);
  }

  String execSelectItemCallback(String messageId, MessageBodySingleSelect message, UserContext uc) {
    return this.callbacks.get(messageId).operation(message, uc);
  }

  // -------------------------

  void createMessage(String id, MessageBody body, SelectItemMessageCallback callback) {
    this.createMessage(id, body);
    this.setMessageCallback(id, callback);
  }

  void createMessage(String id, MessageBody body, Integer delay) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    createMessage(id, header, body, delay);
  }

  void createMessage(
      String id, MessageBody body, Integer delay, SelectItemMessageCallback callback) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    createMessage(id, header, body, delay);
    this.setMessageCallback(id, callback);
  }

  void createMessage(String id, MessageBody body, String avatarName, Integer delay) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    header.avatarName = avatarName;
    createMessage(id, header, body, delay);
  }

  void createMessage(
      String id, MessageBody body, String avatarName, SelectItemMessageCallback callback) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    header.avatarName = avatarName;
    this.setMessageCallback(id, callback);
    createMessage(id, header, body);
  }

  void createMessage(String id, MessageBody body, Image image, Integer delay) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    body.imageURL = image.imageURL;
    body.imageHeight = image.imageHeight;
    body.imageWidth = image.imageWidth;
    createMessage(id, header, body, delay);
  }

  // -------------------------

  void createMessage(String id, MessageBody body) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    createMessage(id, header, body);
  }

  void createMessage(String id, MessageBody body, String avatarName) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    header.avatarName = avatarName;
    createMessage(id, header, body);
  }

  void createMessage(String id, MessageBody body, Image image) {
    MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID, -1); // Default value
    body.imageURL = image.imageURL;
    body.imageHeight = image.imageHeight;
    body.imageWidth = image.imageWidth;
    createMessage(id, header, body);
  }

  void startConversation(UserContext userContext, String startId) {
    log.info("Starting conversation with message: " + startId);
    addToChat(messageList.get(startId), userContext);
  }

  public int getValue(MessageBodyNumber body) {
    return Integer.parseInt(body.text);
  }

  public String getValue(MessageBodySingleSelect body) {

    for (SelectItem o : body.choices) {
      if (SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected) {
        return SelectOption.class.cast(o).value;
      }
    }
    return "";
  }

  public ArrayList<String> getValue(MessageBodyMultipleSelect body) {
    ArrayList<String> selectedOptions = new ArrayList<String>();
    for (SelectItem o : body.choices) {
      if (SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected) {
        selectedOptions.add(SelectOption.class.cast(o).value);
      }
    }
    return selectedOptions;
  }

  public void setExpectedReturnType(String messageId, HedvigDataType type) {
    if (getMessage(messageId) != null) {
      log.debug(
          "Setting the expected return typ for message: "
              + messageId
              + " to "
              + type.getClass().getName());
      getMessage(messageId).expectedType = type;
    } else {
      log.error("ERROR: ------------> Message not found: " + messageId);
    }
  }

  // If the message has a preferred return type it is validated otherwise not
  public boolean validateReturnType(Message m, UserContext userContext) {

    Message mCorr = getMessage(m.id);

    if (mCorr != null) {
      boolean ok = true;
      // All text input are validated to prevent null pointer exceptions
      if (mCorr.body.getClass().equals(MessageBodyText.class)) {
        TextInput t = new TextInput();
        ok = t.validate(m.body.text);
        if (!ok) mCorr.body.text = t.getErrorMessage();
      }
      // Input with explicit validation
      if (mCorr.expectedType != null) {
        ok = mCorr.expectedType.validate(m.body.text);
        if (!ok) mCorr.body.text = mCorr.expectedType.getErrorMessage();
      }
      if (m.body.text == null) {
        m.body.text = "";
      }

      if (!ok) {
        addToChat(m, userContext);
        addToChat(mCorr, userContext);
      }
      return ok;
    }
    return true;
  }

  // ------------------------------------------------------------------------------- //

  public abstract void receiveMessage(UserContext userContext, Message m);

  public void completeRequest(String nxtMsg, UserContext userContext) {
    if (getMessage(nxtMsg) != null) {
      addToChat(getMessage(nxtMsg), userContext);
    }
  }

  public void receiveEvent(EventTypes e, String value, UserContext userContext) {}

  public abstract void init(UserContext userContext);

  public abstract void init(UserContext userContext, String startMessage);

  // ----------------------------------------------------------------------------------------------------------------- //

  public void createChatMessage(String id, MessageBody body) {
    this.createChatMessage(id, body, null);
  }

  /*
   * Splits the message text into separate messages based on \f and adds 'Hedvig is thinking' messages in between
   * */
  public void createChatMessage(String id, MessageBody body, String avatar) {
    String[] paragraphs = body.text.split("\f");
    Integer pId = 0;
    Integer delayFactor = 25; // Milliseconds per character TODO: Externalize this!

    ArrayList<String> msgs = new ArrayList<String>();

    for (int i = 0; i < (paragraphs.length - 1); i++) {
      String s = paragraphs[i];
      String s1 = i == 0 ? id : (id + "." + (pId++).toString());
      String s2 = id + "." + (pId++).toString();
      // log.info("Create message of size "+(s.length())+" with load time:" +
      // (s.length()*delayFactor));
      // createMessage(s1, new MessageBodyParagraph(""), "h_symbol",(s.length()*delayFactor));
      // createMessage(s1, new MessageBodyParagraph(""),(s.length()*delayFactor));
      createMessage(s2, new MessageBodyParagraph(s));

      // if(i==0){
      //	createMessage(s1, new MessageBodyParagraph(""),"h_symbol",(s.length()*delayFactor));
      // }else{
      createMessage(s1, new MessageBodyParagraph(""), (s.length() * delayFactor));
      // }
      msgs.add(s1);
      msgs.add(s2);
    }

    // The 'actual' message
    String sWrite = id + "." + (pId++).toString();
    String sFinal = id + "." + (pId++).toString();
    String s = paragraphs[paragraphs.length - 1]; // Last paragraph is put on actual message
    body.text = s;
    // createMessage(sWrite, new MessageBodyParagraph(""), "h_symbol",(s.length()*delayFactor));
    createMessage(sWrite, new MessageBodyParagraph(""), (s.length() * delayFactor));
    if (avatar != null) {
      createMessage(sFinal, body, avatar);
    } else {
      createMessage(sFinal, body);
    }
    msgs.add(sWrite);
    msgs.add(sFinal);

    // Connect all messages in relay chain
    for (int i = 0; i < (msgs.size() - 1); i++) addRelay(msgs.get(i), msgs.get(i + 1));
  }

  public boolean addMessageFromBackOffice(UserContext uc, String message, String messageId) {
    if (!this.canAcceptAnswerToQuestion(uc)) {
      return false;
    }

    val msg = createBackOfficeMessage(uc, message, messageId);
    uc.getMemberChat().addToHistory(msg);
    return true;
  }

  protected Message createBackOfficeMessage(UserContext uc, String message, String id) {
    Message msg = new Message();
    val selectionItems = getSelectItemsForAnswer(uc);
    msg.body = new MessageBodySingleSelect(message, selectionItems);
    msg.header.fromId = HEDVIG_USER_ID;
    msg.globalId = null;
    msg.header.messageId = null;
    msg.body.id = null;
    msg.id = id;

    return msg;
  }
}
