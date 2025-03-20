package server;

import org.json.JSONArray;
import org.json.JSONObject;

public class Queue {
    private JSONObject queue;
    private APIServer server;
    private int id;

//    public int[] getPosition() {
//        JSONArray queueliste = this.queue.getJSONArray("queue");
//        int[] position = new int[2];
//        long zeit = System.currentTimeMillis() / 1000;
//        long vergangenezeit = zeit - startZeit;
//        JSONObject pos = queueliste.getJSONObject((int)(vergangenezeit / geschwindigkeit));
//        position[0] = pos.getInt("x");
//        position[1] = pos.getInt("y");
//        return position;
//    }
    public Queue(String queue, APIServer server, int id) {
        this.queue = new JSONObject(queue);
        this.server = server;
        this.id = id;

    }
    public void startQueue() {
        JSONArray actions = queue.getJSONArray("queue");
        for (int i = 0; i < actions.length(); i++) {
            JSONObject action = actions.getJSONObject(i);
            int x = action.getInt("x");
            int y = action.getInt("y");
            switch (action.getString("action")) {
                case "mine" -> {
                    this.server.abbauen(x, y, 0, this.id);
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.queue.toString(4);
    }
}
