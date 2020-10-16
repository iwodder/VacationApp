package com.wodder;

public class AirlineReservation {

    private int flightId;
    private String airline;
    private String origin;
    private String dest;
    private boolean booked;

    public AirlineReservation() {

    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public String toString() {
        return "AirlineReservation{" +
                "flightId=" + flightId +
                ", airline='" + airline + '\'' +
                ", origin='" + origin + '\'' +
                ", dest='" + dest + '\'' +
                ", booked=" + booked +
                '}';
    }
}
