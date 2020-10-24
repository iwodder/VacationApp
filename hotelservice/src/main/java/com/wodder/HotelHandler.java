package com.wodder;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class HotelHandler implements Hotels.Iface {

    private final KafkaProducer<String, String> hotelProducer;
    private final KafkaProducer<String, String> msgProducer;
    private String serverIp;
    private final HotelDao hotelDao;

    public HotelHandler() {
        Properties props = hotelProducerProps();
        Properties props2 = msgProducerProps();
        hotelProducer = new KafkaProducer<>(props);
        msgProducer = new KafkaProducer<>(props2);
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverIp = "";
        }
        hotelDao = new HotelDao();
        hotelDao.connectToCollection();
    }

    private Properties hotelProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.133:9091");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    private Properties msgProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.220:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        createLogMessage( "Entering >>> HotelHandler.RemoveReservation(), params [%d]", reservationNum);
        boolean result = hotelDao.removeReservation(reservationNum);
        createLogMessage( "Exiting <<< HotelHandler.RemoveReservation()");
        return result;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        createLogMessage( "Entering >>> HotelHandler.AddReservation(), params [%d, %s]", airlineId, name);
        boolean result = hotelDao.bookHotel(airlineId, name);
        msgProducer.send(new ProducerRecord<>("test", "Hotel reservation added."));
        createLogMessage("Exiting <<< HotelHandler.AddReservation()");
        return result;
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
        List<String> results = hotelDao.getAllHotels();
        createLogMessage("Exiting <<< HotelHandler.GetList()");
        return results;
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
