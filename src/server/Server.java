package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public static final int SERVER_PORT = 5912;
    public static final String MULTICAST_ADDRESS = "228.5.6.7";
    private static final ArrayList<ChatUser> userList = new ArrayList<>();
    public static MessageQueue messageQueue = new MessageQueue();
    private static Admin admin;
    private static Registrar registrar;

    public static void main(String[] args) {

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

    public static void addUser(String userName, String userEmail, String userIp, int userPort) throws IOException {
        ChatUser newUser = new ChatUser(userName, userEmail, userIp, userPort);
        Thread userThread = new Thread(newUser);
        userList.add(newUser);
        userThread.start();
        broadcastMessage(userName + " has connected.");
    }

    static void broadcastMessage(String message) throws IOException {
        messageQueue.addMessage(message);
        for (ChatUser user : userList) {
            user.sendMessage(message);
        }
    }

    static void removeUser(ChatUser userToRemove) throws IOException {
        String userName = userToRemove.getUserName();
        broadcastMessage(userName + " disconnected from the chat.");
        userToRemove.stopUser();
        userList.remove(userToRemove);
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