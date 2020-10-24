package com.wodder;

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

public class AirlineHandler implements Airlines.Iface {

    private final KafkaProducer<String, String> airlineProducer;
    private final KafkaProducer<String, String> msgProducer;
    private final AirlineDao airlineDAO;
    private String serverIp;

    public AirlineHandler() {
        Properties props = getAirlineProps();
        Properties props2 = msgProducerProps();
        airlineProducer = new KafkaProducer<>(props);
        msgProducer = new KafkaProducer<>(props2);
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverIp = "";
        }
        airlineDAO = new AirlineDao();
        airlineDAO.connectToCollection();
    }

    private Properties getAirlineProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.133:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    private Properties msgProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    @Override
    public boolean RemoveReservation(long reservationNum) throws TException {
        createLogMessage( "Entering >>> AirlineHandler.RemoveReservation(), params [%d]", reservationNum);
        boolean result = airlineDAO.removeReservation(reservationNum);
        createLogMessage( "Exiting <<< AirlineHandler.RemoveReservation()");
        return result;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        createLogMessage( "Entering >>> AirlineHandler.AddReservation(), params [%d, %s]", airlineId, name);
        boolean result = airlineDAO.bookFlight(airlineId, name);
        msgProducer.send(new ProducerRecord<>("test", "Adding new airline reservation"));
        createLogMessage("Exiting <<< AirlineHandler.AddReservation()");
        return result;
    }

    @Override
    public String GetReservationList() throws TException {
        createLogMessage( "Entering >>> AirlineHandler.GetReservationList(), params []");
        createLogMessage("Exiting <<< AirlineHandler.GetReservationList()");
        return "No airline reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        createLogMessage( "Entering >>> AirlineHandler.GetList(), params []");
        List<String> results = airlineDAO.getAllAirlines();
        createLogMessage("Exiting <<< AirlineHandler.GetList()");
        return results;
    }

    private void createLogMessage(String msg, Object ... a) {
        String result = String.format("Time[ %s ], Host[ %s ] ", LocalDateTime.now().toString(), serverIp);
        if (a.length > 0) {
            result += String.format(msg, a);
        } else {
            result += msg;
        }
        airlineProducer.send(new ProducerRecord<>("airline-log", result));
    }
}
