package com.wodder;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Logging {
    private static KafkaConsumer<String, String> consumer;
    private static FileWriter fOut;

    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.133:9092");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList("airline-log", "car-log", "hotel-log"));
            System.out.println("Up and running, waiting for messages...");
            fOut = new FileWriter(new File("service.log"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100L));
                for (ConsumerRecord<String, String> r : records) {
                    System.out.println(r.value());
                    fOut.append(r.value());
                }
                fOut.flush();
            }
        } catch (Exception e) {
            if (consumer != null) {
                consumer.close();
            }
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
}
