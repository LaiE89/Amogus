package multiplayer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHeartbeat extends Thread{
    private Server server = null;
    private boolean _isAlive = true;
    private int timeout = 1000;

    public void run() {
        while (!server.serverSocket.isClosed()) {
            pingClients();
        }
    }

    private void pingClients() {
        List<Socket> clientsCopy;
        synchronized (server.lock) {
            clientsCopy = new ArrayList<>(server.clientSockets);
        }
        for (Socket socket : clientsCopy) {
            try {
                if (server.serverDis.get(socket).available() != 0) {
                    System.out.println("CLIENT DISCONNECTED");
                    long t1 = System.currentTimeMillis();
                    System.out.println(server.serverDis.get(socket).read());
                    long t2 = System.currentTimeMillis();
                    System.out.println(";TTL=" + (t2 - t1) + "ms");
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public ServerHeartbeat(Server server) {
        this.server = server;
    }

    public boolean IsAlive() {
        return _isAlive;
    }
}
