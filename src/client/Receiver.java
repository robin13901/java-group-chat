package client;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;

import util.MessageType;
import util.Print;

public class Receiver extends Thread {
    private DataInputStream inputStream = null;
    private volatile boolean shouldRun = true;
    private KeyStore keyStore;
    private User myUser;
    private Print print;
    private Sender senderObject;

    @Override
    public void run() {
        while (shouldRun && !isInterrupted()) {
            try {
                String message = inputStream.readUTF();
                InboundMessage msg = new InboundMessage(message);

                switch (msg.getMessageType()) {
                    case MessageType.USER_MESSAGE -> {
                        String decryptedMsg = decryptMsg(msg.getJsonObject());
                        System.out.println(decryptedMsg);
                    }
                    case MessageType.DELETE_USER -> {
                        deleteUser(msg.getJsonObject());
                    }
                    case MessageType.PUBLIC_KEY -> {
                        addNewUser(msg.getJsonObject());
                    }
                    case MessageType.PUBLIC_KEYS -> {
                        addAllUsers(msg.getJsonObject());
                    }
                    case MessageType.SERVER_INFO -> {
                        String serverInfoString = parseServerInfoMsg(msg.getJsonObject());
                        System.out.println(serverInfoString);
                    }
                    case MessageType.CLOSE_CONNECTION -> {
                        String serverInfoString = parseServerInfoMsg(msg.getJsonObject());
                        System.out.println(serverInfoString);
                        stopReceiver();
                    }
                    case MessageType.UNKNOWN -> {
                    }
                    default -> {
                    }
                }
            } catch (IOException ex) {
                System.err.println("An error occurred while receiving message: " + ex.getMessage());
                stopReceiver();
            }
        }
    }

    public Receiver(User myUser) throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(Client.socket.getInputStream()));
        this.keyStore = myUser.getKeyStore();
        this.myUser = myUser;
        this.print = Print.getInstance();
    }

    public void setSenderThread(Sender senderObject) {
        this.senderObject = senderObject;
    }

    private void stopReceiver() {
        shouldRun = false;
        try {
            inputStream.close();
        } catch (IOException ex) {
            System.err.println("An error occurred while closing receiver input stream: " + ex.getMessage());
        }
        senderObject.stopSenderExtern();
    }

    public boolean stopReceiverExtern() {
        shouldRun = false;
        try {
            inputStream.close();
            return true;
        } catch (IOException ex) {
            System.err.println("An error occurred while closing receiver input stream: " + ex.getMessage());
            return false;
        }
    }

    private void deleteUser(JSONObject content) {
        String userNameToDelete = content.getString("content");
        keyStore.removePublicKeyOfUser(userNameToDelete);
    }

    private void addNewUser(JSONObject content) {
        JSONObject newUserData = content.getJSONObject("content");
        String newUserName = newUserData.getString("name");
        String newUserPublicKey = newUserData.getString("publicKey");

        PublicKey publicKey = KeyGenerator.getPublicKeyFrom(newUserPublicKey);
        keyStore.addPublicKey(newUserName, publicKey);
    }

    private void addAllUsers(JSONObject content) {
        JSONObject allUserData = content.getJSONObject("content");

        Iterator<String> allUserIter = allUserData.keys();
        while (allUserIter.hasNext()) {
            String userName = allUserIter.next();
            PublicKey publicKey = KeyGenerator.getPublicKeyFrom(allUserData.getString(userName));
            keyStore.addPublicKey(userName, publicKey);
        }
    }

    private String parseServerInfoMsg(JSONObject content) {
        String serverInfoMsg = content.getString("content");
        return serverInfoMsg;
    }

    private String decryptMsg(JSONObject content) {
        StringBuilder formattedMessage = new StringBuilder();

        String senderName = content.getString("senderName");
        String timestamp = content.getString("timestamp");

        // format message meta data
        formattedMessage.append(print.bold(print.color(senderName, Color.BLUE)));
        formattedMessage.append(print.color(" [", Color.GRAY));
        formattedMessage.append(print.color(timestamp, Color.GRAY));
        formattedMessage.append(print.color("]: ", Color.GRAY));

        // generate subset only conatining message data
        JSONObject messageContent = content.getJSONObject("content");

        // decrypt message with this user private key
        Iterator<String> allUserIter = messageContent.keys();
        while (allUserIter.hasNext()) {
            String userName = allUserIter.next();
            if (userName.equals(myUser.getName())) {
                try {
                    byte[] decryptedMessageInBytes = CryptoService.decryptString(myUser.getPrivateKey(),
                            messageContent.getString(userName));

                    String decryptedMessageString = new String(decryptedMessageInBytes);

                    formattedMessage.append(decryptedMessageString);
                } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                        | IllegalBlockSizeException
                        | BadPaddingException e) {
                    // Message was not for this user; should be handled otherwise
                }
            }
        }

        return formattedMessage.toString();
    }
}