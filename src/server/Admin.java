package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Admin extends Thread {

    private static final int SERVER_PORT = 9090;
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    public Admin() {}

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);

            while (isRunning) {
                try (Socket clientSocket = serverSocket.accept()) {
                    StringBuilder httpResponse = new StringBuilder("HTTP/1.1 200 OK\r\n\r\n");
                    httpResponse.append("Last 10 Messages: \n\n");
                    int messageCount = 0;

                    for (int i = Server.messageQueue.getStartIndex(); messageCount < 10; i++) {
                        messageCount++;
                        httpResponse.append(Server.messageQueue.getMessage(i)).append("\n");
                    }

                    clientSocket.getOutputStream().write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
                } catch (SocketException sex) {
                    // If Server is closed via stopAdminThread(), then isRunning is false and the server must be closed. 
                    // If isRunning is true, an actual error occured
                    if (isRunning) {
                        Logger.getLogger(Registrar.class.getName()).log(Level.SEVERE, "A PROBLEM OCCURED", sex);
                    }
                    return;
                } catch(Exception e) {
                    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, e);
                }
            }

            System.out.println("Admin server is offline");
        } catch (IOException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopAdminThread() throws IOException {
        isRunning = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}