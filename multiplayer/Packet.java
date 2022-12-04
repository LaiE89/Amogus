package multiplayer;

import java.io.Serializable;

public class Packet implements Serializable {
    int numConnections; // Checking the number of connections in the server
    boolean isGameStarted; // Checking if server started game
    boolean isGameOver; // Checking if player lost
    int sendGarbageLines; // Checking how many lines of garbage this client is sending.

    public Packet(int numConnections, boolean isGameStarted, boolean isGameOver, int sendGarbageLines) {
        this.numConnections = numConnections;
        this.isGameStarted = isGameStarted;
        this.isGameOver = isGameOver;
        this.sendGarbageLines = sendGarbageLines;
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

    public int getSendGarbageLines() {
        return sendGarbageLines;
    }
}
