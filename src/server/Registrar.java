package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Registrar implements Runnable {

    private MulticastSocket multicastSocket;
    private InetAddress multicastGroup;
    private volatile boolean isRunning = true;

    public Registrar() {}

    @Override
    public void run() {
        try {
            multicastGroup = InetAddress.getByName(Server.MULTICAST_ADDRESS);
            multicastSocket = new MulticastSocket(Server.SERVER_PORT);
            multicastSocket.joinGroup(multicastGroup);

            while (isRunning) {
                System.out.println("Waiting for user registration!");
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    multicastSocket.receive(packet);
                } catch (IOException ex) {
                    Logger.getLogger(Registrar.class.getName()).log(Level.SEVERE, null, ex);
                }

                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println(receivedMessage);

                if (!receivedMessage.isEmpty()) {
                    String[] userInfo = receivedMessage.split(",");
                    Server.addUser(userInfo[0], userInfo[1], userInfo[3], Integer.parseInt(userInfo[2]), userInfo[4]);
                }
            }
            System.out.println("User registration is off");
        } catch (IOException ex) {
            Logger.getLogger(Registrar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopUserRegistration() throws IOException {
        isRunning = false;
        multicastSocket.leaveGroup(multicastGroup);
        multicastSocket.close();
    }
}