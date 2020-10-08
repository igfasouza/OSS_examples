package mn.oss.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;

@KafkaClient
@Requires(property = "kafka.sasl.jaas.config")
public interface Producer {
    @Topic("your_topic")
    void sendMessage(@KafkaKey String key, String value);
}