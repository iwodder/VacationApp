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

public class CarHandler implements Cars.Iface {

    private final KafkaProducer<String, String> carProducer;
    private final KafkaProducer<String, String> msgProducer;
    private String serverIp;
    private CarDao carDao;

    public CarHandler() {
        Properties props = carProducerProps();
        Properties props2 = msgProducerProps();
        carProducer = new KafkaProducer<>(props);
        msgProducer = new KafkaProducer<>(props2);
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverIp = "";
        }

        carDao = new CarDao();
        carDao.connectToCollection();
    }

    private Properties carProducerProps() {
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
        createLogMessage("Entering >>> CarHandler.RemoveReservation(), params [%d]", reservationNum);
        boolean result = carDao.removeReservation(reservationNum);
        createLogMessage("Exiting <<< CarHandler.RemoveReservation()");
        return result;
    }

    @Override
    public boolean AddReservation(long airlineId, String name) throws TException {
        createLogMessage("Entering >>> CarHandler.AddReservation(), params [%d, %s]", airlineId, name);
        boolean result = carDao.bookCar(airlineId, name);
        msgProducer.send(new ProducerRecord<>("test", "Adding new car reservations."));
        createLogMessage("Exiting <<< CarHandler.AddReservation()");
        return result;
    }

    @Override
    public String GetReservationList() throws TException {
        createLogMessage("Entering >>> CarHandler.GetReservationList(), params []");
        createLogMessage("Exiting <<< CarHandler.GetReservationList()");
        return "No car reservations right now.";
    }

    @Override
    public List<String> GetList() throws TException {
        createLogMessage("Entering >>> CarHandler.GetList(), params []");
        List<String> results = carDao.getAllCars();
        createLogMessage("Exiting <<< CarHandler.GetList()");
        return results;
    }

    private void createLogMessage(String msg, Object... a) {
        String result = String.format("Time[ %s ], Host[ %s ] ", LocalDateTime.now().toString(), serverIp);
        if (a.length > 0) {
            result += String.format(msg, a);
        } else {
            result += msg;
        }
        carProducer.send(new ProducerRecord<>("car-log", result));
    }
}
