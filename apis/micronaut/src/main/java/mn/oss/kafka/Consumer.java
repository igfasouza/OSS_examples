package mn.oss.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class Consumer {

    @Topic("your_topic")
    public void receive( @KafkaKey String key, String message, long offset, int partition, String topic, long timestamp) {
        System.out.println("********************** Message Incoming **********************");
        System.out.println("Key: " + key);
        System.out.println("Message: " + message);
        System.out.println("Offset: " + offset);
        System.out.println("Partition: " + partition);
        System.out.println("Topic: " + topic);
        System.out.println("Timestamp: " + timestamp);
    }

}