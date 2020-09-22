package com.wodder;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class CarHandler implements Cars.Iface {

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        System.out.printf("Removing car reservation number %d%n", reservationNum);
        return false;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        System.out.printf("Adding car reservation for %s on carId[%d]%n", name, airlineId);
        return true;
    }

    @Override
    public String GetReservationList() throws TException {
        return "No car reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        return new ArrayList<>();
    }
}
