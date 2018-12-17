package com.ali.cs491.carbuds;

public class ChatListUser {
    private int id;
    private int matchId;
    private String name;
    private String surname;
    private String exchange;
    private String queue;
    private String intersectionPolyline;

    public ChatListUser(int id, int matchId, String name, String surname, String exchange, String queue, String intersectionPolyline) {
        this.id = id;
        this.matchId = matchId;
        this.name = name;
        this.surname = surname;
        this.exchange = exchange;
        this.queue = queue;
        this.intersectionPolyline = intersectionPolyline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return surname;
    }

    public void setUsername(String username) {
        this.surname = username;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getIntersectionPolyline() {
        return intersectionPolyline;
    }

    public void setIntersectionPolyline(String intersectionPolyline) {
        this.intersectionPolyline = intersectionPolyline;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
}
