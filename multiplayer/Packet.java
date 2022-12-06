package multiplayer;

import model.TetrisBoard;
import model.TetrisModel;

import java.io.Serializable;
import java.net.Socket;

public class Packet implements Serializable {

    int sender; // Local port of the sender
    int numConnections; // Checking the number of connections in the server
    boolean isGameStarted; // Checking if server started game
    boolean isGameOver; // Checking if player lost
    int sendGarbageLines; // Checking how many lines of garbage this client is sending.
    TetrisBoard senderBoard; // Current board of the sender

    public Packet(int sender, int numConnections, boolean isGameStarted, boolean isGameOver, int sendGarbageLines, TetrisBoard senderBoard) {
        this.sender = sender;
        this.numConnections = numConnections;
        this.isGameStarted = isGameStarted;
        this.isGameOver = isGameOver;
        this.sendGarbageLines = sendGarbageLines;
        this.senderBoard = senderBoard;
    }

    public int getSender() {
        return sender;
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

    public TetrisBoard getSenderBoard() {
        return senderBoard;
    }
}
