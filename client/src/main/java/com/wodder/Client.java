package com.wodder;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Scanner;

public class Client {

    private static Scanner s = new Scanner(System.in);
    public static void main(String[] args) {
        try {
            TTransport airline = new TSocket("191.168.0.13", 55001);
            airline.open();

            TTransport car = new TSocket("191.168.0.12", 55002);
            car.open();

            TTransport hotel = new TSocket("191.168.0.11", 55003);
            hotel.open();

            TProtocol aProtocol = new TBinaryProtocol(airline);
            Airlines.Client aClient = new Airlines.Client(aProtocol);

            TProtocol cProtocol = new TBinaryProtocol(car);
            Cars.Client cClient = new Cars.Client(cProtocol);

            TProtocol hProtocol = new TBinaryProtocol(hotel);
            Hotels.Client hClient = new Hotels.Client(hProtocol);

            System.out.println("Current airline reservations: " + aClient.GetList().toString());
            System.out.println("Adding airline reservation....");
            aClient.AddReservation(1L, "John Doe");

            System.out.println("Enter to continue...");
            s.nextLine();

            System.out.println("Current car reservations: " + cClient.GetList().toString());
            System.out.println("Adding car reservation....");
            cClient.AddReservation(1L, "John Doe");

            System.out.println("Enter to continue...");
            s.nextLine();

            System.out.println("Current hotel reservations: " + hClient.GetList().toString());
            System.out.println("Adding hotel reservation....");
            hClient.AddReservation(1L, "John Doe");
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
