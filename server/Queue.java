package server;

import org.json.JSONArray;
import org.json.JSONObject;

public class Queue {
    private long startZeit;
    private double geschwindigkeit;
    private JSONObject queue;

    public int[] getPosition() {
        JSONArray queueliste = this.queue.getJSONArray("queue");
        int[] position = new int[2];
        long zeit = System.currentTimeMillis() / 1000;
        long vergangenezeit = zeit - startZeit;
        JSONObject pos = queueliste.getJSONObject((int)(vergangenezeit / geschwindigkeit));
        position[0] = pos.getInt("x");
        position[1] = pos.getInt("y");
        return position;
    }
    public Queue(String queue, long startZeit, double geschwindigkeit) {
        this.queue = new JSONObject(queue);
        this.startZeit = startZeit;
        this.geschwindigkeit = geschwindigkeit;
    }

    @Override
    public String toString() {
        return this.queue.toString(4);
    }
}
