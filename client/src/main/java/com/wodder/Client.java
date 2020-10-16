package com.wodder;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Scanner;
import java.util.function.Consumer;

public class Client {
    static TTransport[] airlines = new TTransport[2];
    static Cars.Client cClient;
    static Hotels.Client hClient;
    private static Scanner s = new Scanner(System.in);
    static int idx = 0;

    public static void main(String[] args) {
        try {
            airlines[0] = new TSocket("191.168.0.13", 55001);
            airlines[0].open();

            airlines[1] = new TSocket("191.168.0.14", 55001);
            airlines[1].open();

            TTransport car = new TSocket("191.168.0.12", 55002);
            car.open();

            TTransport hotel = new TSocket("191.168.0.11", 55003);
            hotel.open();

            TProtocol cProtocol = new TBinaryProtocol(car);
            cClient = new Cars.Client(cProtocol);

            TProtocol hProtocol = new TBinaryProtocol(hotel);
            hClient = new Hotels.Client(hProtocol);

            System.out.println("Current airline reservations: ");
            getClient().GetList().forEach(AVAILABILITY_PRINTER);
            System.out.println("Adding airline reservation....");
            getClient().AddReservation(132L, "John Doe");
            System.out.println("Adding airline reservation....");
            getClient().AddReservation(234L, "John Smith");

            System.out.println("Enter to continue...");
            s.nextLine();

            System.out.println("Current car reservations: ");
            cClient.GetList().forEach(AVAILABILITY_PRINTER);
            System.out.println("Adding car reservation....");
            cClient.AddReservation(53L, "John Doe");

            System.out.println("Enter to continue...");
            s.nextLine();

            System.out.println("Current hotel reservations: ");
            hClient.GetList().forEach(AVAILABILITY_PRINTER);
            System.out.println("Adding hotel reservation....");
            hClient.AddReservation(987L, "John Doe");

            long airlineReservationId = 3L;
            long carReservationId = 2L;
            long hotelReservationId = 2L;
            try {
                System.out.println("Adding airline reservation....");
                getClient().AddReservation(airlineReservationId, "John Doe");
                s.nextLine();

                System.out.println("Adding car reservation....");
                cClient.AddReservation(carReservationId, "John Doe");
                s.nextLine();

                System.out.println("Adding hotel reservation....");
                hClient.AddReservation(hotelReservationId, "John Doe");
            } catch (TException e) {
                try {
                    System.out.println("Rolling back reservations, network error encountered");
                    getClient().RemoveReservation(airlineReservationId);
                    cClient.RemoveReservation(carReservationId);
                    System.out.println("Cancelled");
                } catch (TException tException) {
                    System.err.println(" >>>>> Unable to cancel <<<<<");
                    tException.printStackTrace();
                }
            }

        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static final Consumer<String> AVAILABILITY_PRINTER = (r) -> {
        System.out.print("\t");
        System.out.println(r);
    };

    public static Airlines.Client getClient() {
        idx = ((++idx) % airlines.length);
        TProtocol aProtocol = new TBinaryProtocol(airlines[idx]);
        return new Airlines.Client(aProtocol);
    }
}
