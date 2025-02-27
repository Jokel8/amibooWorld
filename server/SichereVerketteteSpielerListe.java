package server;

import economy.Inventory;

public class SichereVerketteteSpielerListe {
    private Node kopf;

    public SichereVerketteteSpielerListe() {
        this.kopf = new Node(new Spieler(null, null, ""));
    }

    public void addSpieler(Spieler spieler) {
        Node next = this.kopf;
        while (next.next != null) {
            next = next.next;
        }
        next.next = new Node(spieler);
    }
    public void removeSpieler(String token) {
        Node next = this.kopf;
        while(!next.next.spieler.getToken().equals(token)) {
            if(next.next == null) {
                return;
            }
            next = next.next;
        }
        next.next = next.next.next;
    }
    public Spieler getSpieler(String token) {
        Node next = this.kopf;
        while(!next.spieler.getToken().equals(token)) {
            if (next.next == null) {
                return null;
            }
            next = next.next;
        }
        return next.spieler;
    }
    public void setInventory(String token, Inventory inventory) {
        Node next = this.kopf;
        while(!next.spieler.getToken().equals(token)) {
            if (next.next == null) {
                return;
            }
            next = next.next;
        }
        next.spieler.inventory = inventory;
    }
    public void setQueue(String token, Queue queue) {
        Node next = this.kopf;
        while(!next.spieler.getToken().equals(token)) {
            if (next.next == null) {
                return;
            }
            next = next.next;
        }
        next.spieler.queue = queue;
    }
    public void speichern(Datenbank datenbank) {
        Node next = this.kopf.next;
        while (next != null) {
            String query = "UPDATE user SET user_inventory = " + next.spieler.inventory.toString() + " WHERE user_token = " + next.spieler.getToken() + ";";
            datenbank.updateMachen(query);
        }
    }
}
