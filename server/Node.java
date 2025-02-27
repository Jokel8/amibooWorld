package server;

public class Node {
    private volatile boolean gesperrt;
    public volatile Spieler spieler;
    public volatile Node next;

    public synchronized boolean setGesperrt() {
        if (this.gesperrt) {
            return false;
        } else {
            this.gesperrt = true;
            return true;
        }
    }
    public boolean isGesperrt() {
        return this.gesperrt;
    }
    public Node(Spieler spieler) {
        this.spieler = spieler;
    }
}
