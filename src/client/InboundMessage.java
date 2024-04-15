package client;

import org.json.JSONException;
import org.json.JSONObject;

import util.MessageType;

public class InboundMessage {
    private final String msgString;
    private String content;
    private MessageType msgType;
    private JSONObject msgJsonObject;
    
    public InboundMessage(String msg) {
        this.msgString = msg;
        this.msgJsonObject = new JSONObject(this.msgString);
        try {
            this.msgType = this.msgJsonObject.getEnum(MessageType.class, "msgType");   
        } catch (JSONException e) {
            this.msgType = MessageType.UNKNOWN;
        }
    }

    public String getMsgContent() {
        return this.content;
    }

    public MessageType getMessageType() {
        return this.msgType;
    }

    public JSONObject getJsonObject() {
        return this.msgJsonObject;
    }
}
