package com.wodder;

import com.wodder.Airlines.*;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class AirlineServer {

    public static AirlineHandler airlineHandler;
    public static Airlines.Processor<AirlineHandler> processor;

    public static void main(String[] args) {
        try {
            airlineHandler = new AirlineHandler();
            processor = new Airlines.Processor<>(airlineHandler);

            TServerTransport serverTransport = new TServerSocket(55001);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Airline server is up and running...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
