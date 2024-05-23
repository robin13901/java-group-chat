package server;

import java.awt.Color;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import util.MessageType;
import util.Print;

public class Server {
    public static final int SERVER_PORT = 5912;
    public static final String MULTICAST_ADDRESS = "228.5.6.7";
    private static final ArrayList<ChatUser> userList = new ArrayList<>();
    public static MessageQueue messageQueue = new MessageQueue();
    private static Admin admin;
    private static Registrar registrar;
    private static Print print;

    private static KeyStore keyStore;

    public static void main(String[] args) {
        keyStore = new KeyStore();

        print = Print.getInstance();
        registrar = new Registrar();
        Thread registrationThread = new Thread(registrar);
        registrationThread.start();

        admin = new Admin();
        Thread adminThread = new Thread(admin);
        adminThread.start();

        try {
            listenForUserInput();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PublicKey getKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void addUser(String userName, String userEmail, String userIp, int userPort, String publicKeyString) throws IOException {
        // Broadcast: new user
        JSONObject connectionInfoJson = new JSONObject();
        connectionInfoJson.put("msgType", MessageType.SERVER_INFO);
        connectionInfoJson.put("content", print.bold(print.color("INFO: " + userName + " has connected.", Color.GREEN)));
        broadcastMessage(connectionInfoJson.toString());

        keyStore.addPublicKey(userName, getKey(publicKeyString));

        // Send public key of new user to all current clients
        JSONObject newUserBroadcastJson = new JSONObject();
        newUserBroadcastJson.put("msgType", MessageType.PUBLIC_KEY);

        JSONObject newUserDataJson = new JSONObject();
        newUserDataJson.put("name", userName);
        newUserDataJson.put("publicKey",
                Base64.getEncoder().encodeToString(keyStore.getPublicKeyForUser(userName).getEncoded()));

        newUserBroadcastJson.put("content", newUserDataJson);

        broadcastMessage(newUserBroadcastJson.toString());

        // create new user
        ChatUser newUser = new ChatUser(userName, userEmail, userIp, userPort);
        Thread userThread = new Thread(newUser);
        userList.add(newUser);
        userThread.start();

        // send public keys of all users to newly connected user and update all users
        // with new public key of joined user
        JSONObject publicKeysJson = new JSONObject();
        publicKeysJson.put("msgType", MessageType.PUBLIC_KEYS);

        JSONObject publicKeysDataJson = new JSONObject();
        StringBuilder currentClientNames = new StringBuilder();
        for (Map.Entry<String, PublicKey> entry : keyStore.getMap().entrySet()) { 
            // here we could not send the new user, so that one user receives its own messages. If we dont add the new user, then we have to consider this in the receiver as well, since an empty message would appear
            publicKeysDataJson.put(entry.getKey(), Base64.getEncoder().encodeToString(entry.getValue().getEncoded()));
            currentClientNames.append(entry.getKey() + ", ");
        }
        publicKeysJson.put("content", publicKeysDataJson);

        JSONObject publicKeysInfoJson = new JSONObject();
        publicKeysInfoJson.put("msgType", MessageType.SERVER_INFO);
        publicKeysInfoJson.put("content", print.bold(print.color("Active Users: " + currentClientNames.toString(), Color.GREEN)));

        newUser.sendMessage(publicKeysJson.toString());
        newUser.sendMessage(publicKeysInfoJson.toString());
    }

    static void broadcastMessage(String message) throws IOException {
        messageQueue.addMessage(message);
        for (ChatUser user : userList) {
            user.sendMessage(message);
        }
    }

    static void removeUser(ChatUser userToRemove) throws IOException {
        String userName = userToRemove.getUserName();
        userToRemove.stopUser();
        userList.remove(userToRemove);
        keyStore.removePublicKeyOfUser(userName);

        // inform clients that user left
        JSONObject leftUserInfoMsgJson = new JSONObject();
        leftUserInfoMsgJson.put("msgType", MessageType.SERVER_INFO);
        leftUserInfoMsgJson.put("content",
                print.bold(print.color("INFO " + userName + " disconnected from the chat.", Color.RED)));
        broadcastMessage(leftUserInfoMsgJson.toString());

        // Command: remove key of removed user
        JSONObject rmvUserCmdJson = new JSONObject();
        rmvUserCmdJson.put("msgType", MessageType.DELETE_USER);
        rmvUserCmdJson.put("content", userName);
        broadcastMessage(rmvUserCmdJson.toString());

        System.out.println(print.color("Removed user: " + userName, Color.RED));
    }

    public static void listenForUserInput() throws Exception {
        Scanner consoleInput = new Scanner(System.in);
        while (true) {
            while (!consoleInput.hasNextLine()) {
                Thread.sleep(1);
            }
            String input = consoleInput.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                break;
            }
        }
        consoleInput.close();
        shutDownServer();
    }

    public static void shutDownServer() {
        try {
            JSONObject closeConnectionInfoJson = new JSONObject();
            closeConnectionInfoJson.put("msgType", MessageType.CLOSE_CONNECTION);
            closeConnectionInfoJson.put("content", print.bold(print.color("Server closed the connection!", Color.RED)));
            broadcastMessage(closeConnectionInfoJson.toString());
            registrar.stopUserRegistration();
            // for (ChatUser user : userList) {
            //     user.stopUser();
            // }
            userList.clear();
            admin.stopAdminThread();
            System.out.println("Server is offline");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}