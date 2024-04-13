package client;

import java.util.Arrays;

public class InboundMessage {
    private final String msg;
    private String content;
    private MessageType msgType;
    
    public InboundMessage(String msg) {
        this.msg = msg;
    }

    public void unpackMsg() {
        String[] msg_split = msg.split(" ");
        switch (msg_split[0]) {
            case "DEL" -> parseDeleteUserPublicKey();
            case "PUBLIC" -> parseUsersPublicKeys();
            case "NEW" -> parseUserPublicKey();
            case "INFO" -> parseInfoMsg();
            default -> parseMsg();
        }
    }

    public String getMsgContent() {
        return this.content;
    }

    public MessageType getMessageType() {
        return this.msgType;
    }

    private void parseUserPublicKey() {
        String[] msg_split = this.msg.split(" ");
        content = msg_split[2];

        this.msgType = MessageType.PUBLIC_KEY;
    }

    private void parseUsersPublicKeys() {
        String[] msg_split = this.msg.split(" ");
        content = String.join(" ", Arrays.copyOfRange(msg_split, 2, msg_split.length));

        this.msgType = MessageType.PUBLIC_KEYS;
    }

    private void parseDeleteUserPublicKey() {
        String[] msg_split = this.msg.split(" ");
        content = msg_split[2];

        this.msgType = MessageType.DELETE_USER;
    }

    private void parseInfoMsg() {
        this.content = this.msg;
        this.msgType = MessageType.SERVER_INFO;
    }

    private void parseMsg() {
        this.content = this.msg;
        this.msgType = MessageType.USER_MESSAGE;
    }
}
