package client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
        StringBuilder messageBuilder = new StringBuilder();
        
        // message meta data
        messageBuilder.append(this.userName);
        messageBuilder.append(" ");
        messageBuilder.append(this.timestamp);

        // encrypted message per user who shoudl receive the message
        for (Map.Entry<String, PublicKey> entry : keyStore.getMap().entrySet()) {
            try {
                encryptedMsg = CryptoService.encryptString(entry.getValue(), this.msg);

                messageBuilder.append(" ");
                messageBuilder.append(entry.getKey());
                messageBuilder.append(" ");
                messageBuilder.append(encryptedMsg);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return messageBuilder.toString();
    }
}
