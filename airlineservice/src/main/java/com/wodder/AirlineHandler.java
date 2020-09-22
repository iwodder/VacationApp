package com.wodder;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class AirlineHandler implements Airlines.Iface {

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        System.out.printf("Removing airline reservation number %d%n", reservationNum);
        return false;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        System.out.printf("Adding airline reservation for %s on airlineId[%d]%n", name, airlineId);
        return false;
    }

    @Override
    public String GetReservationList() throws TException {
        return "No airline reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        return new ArrayList<>();
    }
}
