package com.oss.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;

public class Consumer {

    private final KafkaConsumer<Integer, String> consumer;
    private final String topic;

    public Consumer(String topic) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "streaming.{region}.oci.oraclecloud.com:9092");
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"{tenancyName}/{username}/{stream pool OCID}\" password=\"{authToken}\";");
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(properties);
        this.topic = topic;
    }

    public void testConsumer() {

        consumer.subscribe(Collections.singleton(topic));

        while(true){
            ConsumerRecords<Integer, String> poll = consumer.poll(100);
            for(ConsumerRecord r : poll) {
                System.out.println("key : " + r.key());
                System.out.println(r);
            }
        }

    }

    public static void main(String[] args) {
        String topic = "topic";
        Consumer simpleHLConsumer = new Consumer(topic);
        simpleHLConsumer.testConsumer();
    }

}