package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Receiver extends Thread {
    private DataInputStream inputStream = null;
    private volatile boolean shouldRun = true;

    @Override
    public void run() {
        while (shouldRun) {
            try {
                String message = inputStream.readUTF();
                System.out.println(message);
//                Client.msgUi.updateMsg(message);
            } catch (IOException ex) {
                System.err.println("An error occurred while receiving message: " + ex.getMessage());
                stopReceiver();
            }
        }
    }

    public Receiver() throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(Client.socket.getInputStream()));
    }

    public void stopReceiver() {
        shouldRun = false;
        try {
            inputStream.close();
        } catch (IOException ex) {
            System.err.println("An error occurred while closing receiver input stream: " + ex.getMessage());
        }
    }
}