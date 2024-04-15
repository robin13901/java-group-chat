package client;

import java.io.IOException;
import java.net.ServerSocket;

public class TestUser1 extends Client {

    public static void main(String[] args) throws IOException {
        user = new User("Alice", "alice.blub@secure.net");

        serverSocket = new ServerSocket(0);
        sendConnectionDetails(serverSocket.getLocalPort(), serverSocket.getInetAddress().getHostName());
        socket = serverSocket.accept();
        System.out.println("Connected!");
        sender = new Sender(user);
        Thread senderThread = new Thread(sender);
        senderThread.start();
        receiver = new Receiver(user);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

}