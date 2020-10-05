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

public class HotelHandler implements Hotels.Iface {

    private final KafkaProducer<String, String> hotelProducer;
    private String serverIp;

    public HotelHandler() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.133:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        hotelProducer = new KafkaProducer<>(props);
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverIp = "";
        }

    }

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        createLogMessage( "Entering >>> HotelHandler.RemoveReservation(), params [%d]", reservationNum);
        System.out.printf("Removing hotel reservation %d%n", reservationNum);
        createLogMessage( "Exiting <<< HotelHandler.RemoveReservation()");
        return true;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        createLogMessage( "Entering >>> HotelHandler.AddReservation(), params [%d, %s]", airlineId, name);
        System.out.printf("Adding hotel reservation for %s on hotelId[%d]%n", name, airlineId);
        createLogMessage("Exiting <<< HotelHandler.AddReservation()");
        return false;
    }

    @Override
    public String GetReservationList() throws TException {
        createLogMessage( "Entering >>> HotelHandler.GetReservationList(), params []");
        createLogMessage("Exiting <<< HotelHandler.GetReservationList()");
        return "No hotel reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        createLogMessage( "Entering >>> HotelHandler.GetList(), params []");
        createLogMessage("Exiting <<< HotelHandler.GetList()");
        return new ArrayList<>();
    }

    private void createLogMessage(String msg, Object ... a) {
        String result = String.format("Time[ %s ], Host[ %s ] ", LocalDateTime.now().toString(), serverIp);
        if (a.length > 0) {
            result += String.format(msg, a);
        } else {
            result += msg;
        }
        hotelProducer.send(new ProducerRecord<>("hotel-log", result));
    }
}
