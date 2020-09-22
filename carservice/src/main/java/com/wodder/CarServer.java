package com.wodder;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class CarServer {
    public static CarHandler carHandler;
    public static Cars.Processor<CarHandler> processor;

    public static void main(String[] args) {
        try {
            carHandler = new CarHandler();
            processor = new Cars.Processor<>(carHandler);

            TServerTransport serverTransport = new TServerSocket(55002);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Car server is up and running...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
