package com.wodder;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarHandler implements Cars.Iface {

    private final KafkaProducer<String, String> carProducer;
    private String serverIp;

    public CarHandler() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.133:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        carProducer = new KafkaProducer<>(props);
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverIp = "";
        }

    }

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        createLogMessage( "Entering >>> CarHandler.RemoveReservation(), params [%d]", reservationNum);
        System.out.printf("Removing car reservation number %d%n", reservationNum);
        createLogMessage( "Exiting <<< CarHandler.RemoveReservation()");
        return false;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        createLogMessage( "Entering >>> CarHandler.AddReservation(), params [%d, %s]", airlineId, name);
        System.out.printf("Adding car reservation for %s on carId[%d]%n", name, airlineId);
        createLogMessage("Exiting <<< CarHandler.AddReservation()");
        return true;
    }

    @Override
    public String GetReservationList() throws TException {
        createLogMessage( "Entering >>> CarHandler.GetReservationList(), params []");
        createLogMessage("Exiting <<< CarHandler.GetReservationList()");
        return "No car reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        createLogMessage( "Entering >>> CarHandler.GetList(), params []");
        createLogMessage("Exiting <<< CarHandler.GetList()");
        return new ArrayList<>();
    }

    private void createLogMessage(String msg, Object ... a) {
        String result = String.format("Time[ %s ], Host[ %s ] ", LocalDateTime.now().toString(), serverIp);
        if (a.length > 0) {
            result += String.format(msg, a);
        } else {
            result += msg;
        }
        carProducer.send(new ProducerRecord<>("car-log", result));
    }
}
