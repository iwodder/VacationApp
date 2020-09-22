package com.wodder;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class HotelHandler implements Hotels.Iface {

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        System.out.printf("Removing hotel reservation %d%n", reservationNum);
        return true;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        System.out.printf("Adding hotel reservation for %s on hotelId[%d]%n", name, airlineId);
        return false;
    }

    @Override
    public String GetReservationList() throws TException {
        return "No hotel reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        return new ArrayList<>();
    }
}
