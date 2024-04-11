package server;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatUser extends Thread {
    private Print print;
    private String userName;
    private String userEmail;
    private String userIp;
    private int userPort;
    private Socket userSocket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private volatile boolean isRunning = true;

    public ChatUser(String name, String email, String ip, int port) {
        print = Print.getInstance();
        this.userName = name;
        this.userEmail = email;
        this.userIp = ip;
        this.userPort = port;

        try {
            userSocket = new Socket(this.userIp, this.userPort);
            inputStream = new DataInputStream(new BufferedInputStream(userSocket.getInputStream()));
            outputStream = new DataOutputStream(userSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ChatUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String message = inputStream.readUTF();

                if (message.equals("/disconnected")) {
                    Server.removeUser(this);
                    break;
                }

                System.out.println(print.color(this.userName + ": ", Color.ORANGE) + message);
                Server.broadcastMessage(print.color(this.userName + ": ", Color.ORANGE) + message);

            } catch (IOException ex) {
                System.out.println(this.getUserName() + " disconnected.");
                try {
                    Server.removeUser(this);
                } catch (IOException ex1) {
                    Logger.getLogger(ChatUser.class.getName()).log(Level.SEVERE, null, ex1);
                }
                break;
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String ip) {
        this.userIp = ip;
    }

    public int getUserPort() {
        return userPort;
    }

    public void setUserPort(int port) {
        this.userPort = port;
    }

    public void sendMessage(String message) throws IOException {
        try {
            outputStream.writeUTF(message);
        } catch (IOException ex) {
            System.out.println(this.getUserName() + " disconnected.");
            Server.removeUser(this);
        }
    }

    public void stopUser() throws IOException {
        isRunning = false;
        inputStream.close();
        outputStream.close();
        userSocket.close();
    }
}