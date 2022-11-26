package multiplayer;

import java.io.Serializable;

public class Packet implements Serializable {
    int numConnections; // Checking the number of connections in the server
    boolean isGameStarted; // Checking if server started game
    boolean isGameOver; // Checking if player lost
    public Packet(int numConnections, boolean isGameStarted, boolean isGameOver) {
        this.numConnections = numConnections;
        this.isGameStarted = isGameStarted;
        this.isGameOver = isGameOver;
        //this.disconnectedPort = disconnectedPort;
    }

    public int getNumConnections() {
        return numConnections;
    }

    public boolean getIsGameStarted() {
        return isGameStarted;
    }

    public boolean getIsGameOver() {
        return isGameOver;
    }
    /*public int getDisconnectedPort() {
        return disconnectedPort;
    }*/
}
