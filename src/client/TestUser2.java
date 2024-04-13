package client;

import java.io.IOException;
import java.net.ServerSocket;

public class TestUser2 extends Client {

    public static void main(String[] args) throws IOException {
        user = new User("Bob", "bob.schlob@unsecure.net");

        serverSocket = new ServerSocket(0);
        sendConnectionDetails(serverSocket.getLocalPort(), serverSocket.getInetAddress().getHostName());
        socket = serverSocket.accept();
        System.out.println("Connected!");
        sender = new Sender();
        Thread senderThread = new Thread(sender);
        senderThread.start();
        receiver = new Receiver();
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

}