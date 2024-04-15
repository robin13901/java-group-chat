package client;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.Print;

public class Sender extends Thread {

    private DataOutputStream outputStream = null;
    private volatile boolean shouldRun = true;
    private KeyStore keyStore;
    private User myUser;
    private Print print;

    public Sender(User myUser) {
        this.keyStore = myUser.getKeyStore();
        this.myUser = myUser;
        this.print = Print.getInstance();
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            outputStream = new DataOutputStream(Client.socket.getOutputStream());
            while (shouldRun) {
                String message = scanner.nextLine();
                if (message.isEmpty()) {
                    System.out.println(print.bold(print.color("Empty messages not support!", Color.red)));
                    continue;
                }
                if (message.equals("/quit") || message.equals("/disconnect")) {
                    stopSender();
                    break;
                }
                OutboundMessage outboundMessage = new OutboundMessage(message, myUser.getName());

                String packedMsg = outboundMessage.packMsg(keyStore);

                outputStream.writeUTF(packedMsg);
            }
            System.out.println("Sender is off");
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(String msg) {
        try {
            outputStream.writeUTF(msg);
        } catch (IOException ex) {
            System.out.println("Problem connecting to the server.");
        }
    }

    public void stopSender() {
        sendMsg("/disconnected");
        shouldRun = false;
        try {
            outputStream.close();
        } catch (IOException ex) {
            System.out.println("An error occurred while closing sender output stream: " + ex.getMessage());
        }
        System.out.println("Sender closed");
    }
}