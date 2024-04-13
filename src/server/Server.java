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

    public static void addUser(String userName, String userEmail, String userIp, int userPort, String publicKeyString)
            throws IOException {
        ChatUser newUser = new ChatUser(userName, userEmail, userIp, userPort);
        Thread userThread = new Thread(newUser);
        userList.add(newUser);
        userThread.start();
        broadcastMessage(print.bold(print.color("INFO " + userName + " has connected.", Color.GREEN)));
        keyStore.addPublicKey(userName, getKey(publicKeyString));

        // send public keys of all users to newly connected user and update all users
        // with new public key of joined user
        StringBuilder publicKeyStrings = new StringBuilder();
        publicKeyStrings.append("PUBLIC KEYS ");
        for (Map.Entry<String, PublicKey> entry : keyStore.getMap().entrySet()) {
            publicKeyStrings
                    .append(entry.getKey() + ":" + Base64.getEncoder().encodeToString(entry.getValue().getEncoded()));
            publicKeyStrings.append(" ");
        }
        newUser.sendMessage(publicKeyStrings.toString());

        broadcastMessage("NEW USER " + userName + ":"
                + Base64.getEncoder().encodeToString(keyStore.getPublicKeyForUser(userName).getEncoded()));
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
        broadcastMessage(print.bold(print.color("INFO " + userName + " disconnected from the chat.", Color.RED)));

        // inform clients that user left
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
            broadcastMessage("/offline");
            registrar.stopUserRegistration();
            for (ChatUser user : userList) {
                user.stopUser();
            }
            userList.clear();
            admin.stopAdminThread();
            System.out.println("Server is offline");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}