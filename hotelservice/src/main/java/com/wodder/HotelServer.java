package com.wodder;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class HotelServer {
    public static HotelHandler hotelHandler;
    public static Hotels.Processor<HotelHandler> processor;

    public static void main(String[] args) {
        try {
            hotelHandler = new HotelHandler();
            processor = new Hotels.Processor<>(hotelHandler);

            TServerTransport serverTransport = new TServerSocket(55003);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Hotel server is up and running...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
