package client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;

import util.MessageType;

public class OutboundMessage {
    private final String msg;
    private final Timestamp timestamp;
    private final String userName;

    public OutboundMessage(String msg, String userName) {
        this.msg = msg;
        this.userName = userName;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String packMsg(KeyStore keyStore) {
        String encryptedMsg = "";
        JSONObject messageJSON = new JSONObject();
        JSONObject messageDataJSON = new JSONObject();

        // message meta data
        messageJSON.put("msgType", MessageType.USER_MESSAGE);
        messageJSON.put("senderName", this.userName);
        messageJSON.put("timestamp", this.timestamp);

        // encrypted message per user who shoudl receive the message
        for (Map.Entry<String, PublicKey> entry : keyStore.getMap().entrySet()) {
            try {
                encryptedMsg = CryptoService.encryptString(entry.getValue(), this.msg);
                messageDataJSON.put(entry.getKey(), encryptedMsg);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        messageJSON.put("content", messageDataJSON);

        return messageJSON.toString();
    }
}
