package com.ali.cs491.carbuds.Messages;

public class ChatListUser {
    private int id;
    private int matchId;
    private String name;
    private String surname;
    private String exchange;
    private String queue;
    private String intersectionPolyline;
    private String tripStart;
    private String startPoint;
    private String endPoint;
    private boolean isDriver;

    public ChatListUser(int id, int matchId, String name, String surname, String exchange,
                        String queue, String intersectionPolyline,
                        String tripStart, String startPoint, String endPoint, boolean isDriver) {
        this.id = id;
        this.matchId = matchId;
        this.name = name;
        this.surname = surname;
        this.exchange = exchange;
        this.queue = queue;
        this.intersectionPolyline = intersectionPolyline;
        this.tripStart = tripStart;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.isDriver = isDriver;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTripStart() {
        return tripStart;
    }

    public void setTripStart(String trip_start) {
        this.tripStart = trip_start;
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
