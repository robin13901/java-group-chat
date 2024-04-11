package client;

import java.io.IOException;
import java.net.*;

public class Client {

    static final int PORT = 5912;
    static final String MULTICAST_ADDRESS = "228.5.6.7";

    static ServerSocket serverSocket = null;
    static Socket socket = null;

    static User user = null;
    static Sender sender;
    static Receiver receiver;

    public static void main(String[] args) throws IOException {
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

    static void sendConnectionDetails(int port, String hostname) {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            InetAddress multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
            String message = user.getName() + "," + user.getEmail() + "," + port + "," + hostname;
            byte[] data = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, multicastGroup, PORT);
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}