package client;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import util.Print;

public class Receiver extends Thread {
    private DataInputStream inputStream = null;
    private volatile boolean shouldRun = true;
    private KeyStore keyStore;
    private User myUser;
    private Print print;

    @Override
    public void run() {
        while (shouldRun) {
            try {
                String message = inputStream.readUTF();
                InboundMessage msg = new InboundMessage(message);

                msg.unpackMsg();

                switch (msg.getMessageType()) {
                    case MessageType.USER_MESSAGE -> {
                        System.out.println(decryptMsg(msg.getMsgContent()));
                    }
                    case MessageType.DELETE_USER -> {
                        deleteUser(msg.getMsgContent());
                    }
                    case MessageType.PUBLIC_KEY -> {
                        addNewUser(msg.getMsgContent());
                    }
                    case MessageType.PUBLIC_KEYS -> {
                        addAllUsers(msg.getMsgContent());
                    }
                    case MessageType.SERVER_INFO -> {
                        System.out.println(msg.getMsgContent());
                    }
                    default -> System.out.println();
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

    public void stopReceiver() {
        shouldRun = false;
        try {
            inputStream.close();
        } catch (IOException ex) {
            System.err.println("An error occurred while closing receiver input stream: " + ex.getMessage());
        }
    }

    private void deleteUser(String userToRemove) {
        keyStore.removePublicKeyOfUser(userToRemove);
    }

    private void addNewUser(String userProps) {
        String[] msg_split = userProps.split(" ");
        for (String string : msg_split) {
            if (string.equals("NEW") || string.equals("USER") || string.equals(myUser.getName())) {
                continue;
            }
            String[] userKeyPair = string.split(":");
            String userName = userKeyPair[0];
            String userPublicKey = userKeyPair[1];
            PublicKey pk = KeyGenerator.getPublicKeyFrom(userPublicKey);
            keyStore.addPublicKey(userName, pk);
        }

    }

    private void addAllUsers(String usersProps) {
        String[] msg_split = usersProps.split(" ");
        for (String string : msg_split) {
            if (string.equals("PUBLIC") || string.equals("KEYS") || string.equals(myUser.getName()))
                continue;
            String[] userKeyPair = string.split(":");
            String userName = userKeyPair[0];
            String userPublicKey = userKeyPair[1];
            PublicKey pk = KeyGenerator.getPublicKeyFrom(userPublicKey);
            keyStore.addPublicKey(userName, pk);
        }
    }

    private String decryptMsg(String rawMessage) {
        StringBuilder formattedMessage = new StringBuilder();
        String[] msg_split = rawMessage.split(" ");

        if (msg_split.length < 5)
            return rawMessage;

        String senderName = msg_split[0];
        String msgDate = msg_split[1];
        String msgTime = msg_split[2];

        // format message meta data
        formattedMessage.append(print.bold(print.color(senderName, Color.BLUE)));
        formattedMessage.append(print.color(": [", Color.GRAY));
        formattedMessage.append(print.color(msgDate, Color.GRAY));
        formattedMessage.append(print.color(" ", Color.GRAY));
        formattedMessage.append(print.color(msgTime, Color.GRAY));
        formattedMessage.append(print.color("] ", Color.GRAY));

        // generate subset only conatining message data
        String[] messageData = Arrays.copyOfRange(msg_split, 3, msg_split.length);

        // decrypt message with this user private key
        for (int i = 0; i < messageData.length; i += 2) {
            String receiverName = messageData[i];
            if (receiverName.equals(myUser.getName())) {
                String encryptedMessage = messageData[i + 1];
                try {
                    byte[] decryptedMessageInBytes = CryptoService.decryptString(myUser.getPrivateKey(),
                            encryptedMessage);
                    String decryptedMessageString = new String(decryptedMessageInBytes);

                    formattedMessage.append(decryptedMessageString);
                } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                        | IllegalBlockSizeException
                        | BadPaddingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return formattedMessage.toString();
    }
}