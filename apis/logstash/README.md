# Using Logstash with Oracle Streaming Service for Apache Kafka Ecosystems

This tutorial will walk you through integrating Logstash with Kafka-enabled Oracle Streaming Service OSS using Logstash Kafka input/output plugins.

## Prerequisites

If you don't have an OCI subscription, create a [free account](https://www.oracle.com/cloud/free/) before you begin.

In addition:

* [Java Development Kit (JDK) 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
    * Be sure to set the JAVA_HOME environment variable to point to the folder where the JDK is installed.
* [Logstash](https://www.elastic.co/guide/en/logstash/current/installing-logstash.html)
    * Unpack the download file and add the Logstash binaries directory, e.g. `<unpacked_file_path>/logstash-6.6.1/bin`, to your `PATH` environment variable.
    * Kafka input/output plugins, `logstash-input-kafka` and `logstash-output-kafka`, are ususlly already included in common plugins, which you can use directly. You can verify whether they are appropriately installed by running `logstash-plugin list 'kafka'`.
    * In case of the above plugins not included in common plugins, you can run `logstash-plugin install logstash-input-kafka` or `logstash-plugin install logstash-output-kafka` to install Kafka input/output plugins.


## Logstash Producer

Using the provided Logstash producer example, send messages to the OSS.

#### logstashProducer.config

Update the `bootstrap_servers` value in `logstashProducer.config` to direct the producer to the OSS endpoint.

```
kafka {
    codec => json
    topic_id => "mytopic"
    bootstrap_servers => "streaming.{region}.oci.oraclecloud.com:9092"
    security_protocol => "SASL_SSL"
    sasl_mechanism => "PLAIN"
    jaas_path => "<path_to_jaas_file>"
}
```

Update the `password` value in `jaas.conf` to use the correct authentication.

```
KafkaClient {
    org.apache.kafka.common.security.plain.PlainLoginModule required
	username="{tenancyName}/{username}/{compartmentId}" 
	password="{authToken}";
};
```

#### Configuration issues

You may not be able to configure the jaas field due to this [Kafka output plugin issue](https://github.com/logstash-plugins/logstash-output-kafka/issues/215). In this case, one possible quick fix is to edit the `kafka.rb` file in `logstash-output-kafka` plugin folder, e.g. `logstash-6.6.1/vendor/bundle/jruby/2.3.0/gems/logstash-output-kafka-7.3.1/lib/logstash/outputs/kafka.rb`. You may directly set the `sasl.jaas.config` in the `create_producer` function and comment out `set_trustore_keystore_config(props)` if you set `security_protocol` as `SASL_SSL`.

```
def create_producer
    begin
        props = java.util.Properties.new
        kafka = org.apache.kafka.clients.producer.ProducerConfig

        props.put(kafka::ACKS_CONFIG, acks)
        props.put(kafka::BATCH_SIZE_CONFIG, batch_size.to_s)
        props.put(kafka::BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers)
        props.put(kafka::BUFFER_MEMORY_CONFIG, buffer_memory.to_s)
        props.put(kafka::COMPRESSION_TYPE_CONFIG, compression_type)
        props.put(kafka::CLIENT_ID_CONFIG, client_id) unless client_id.nil?
        props.put(kafka::KEY_SERIALIZER_CLASS_CONFIG, key_serializer)
        props.put(kafka::LINGER_MS_CONFIG, linger_ms.to_s)
        props.put(kafka::MAX_REQUEST_SIZE_CONFIG, max_request_size.to_s)
        props.put(kafka::METADATA_MAX_AGE_CONFIG, metadata_max_age_ms) unless metadata_max_age_ms.nil?
        props.put(kafka::RECEIVE_BUFFER_CONFIG, receive_buffer_bytes.to_s) unless receive_buffer_bytes.nil?
        props.put(kafka::RECONNECT_BACKOFF_MS_CONFIG, reconnect_backoff_ms) unless reconnect_backoff_ms.nil?
        props.put(kafka::REQUEST_TIMEOUT_MS_CONFIG, request_timeout_ms) unless request_timeout_ms.nil?
        props.put(kafka::RETRIES_CONFIG, retries.to_s) unless retries.nil?
        props.put(kafka::RETRY_BACKOFF_MS_CONFIG, retry_backoff_ms.to_s) 
        props.put(kafka::SEND_BUFFER_CONFIG, send_buffer_bytes.to_s)
        props.put(kafka::VALUE_SERIALIZER_CLASS_CONFIG, value_serializer)

        props.put("security.protocol", security_protocol) unless security_protocol.nil?
        props.put("sasl.jaas.config", 'org.apache.kafka.common.security.plain.PlainLoginModule required username="{tenancyName}/{username}/{compartmentId}" password="{authToken}";')

        if security_protocol == "SSL"
            set_trustore_keystore_config(props)
        elsif security_protocol == "SASL_PLAINTEXT"
            set_sasl_config(props)
        elsif security_protocol == "SASL_SSL"
            #set_trustore_keystore_config(props)
            set_sasl_config(props)
        end

        org.apache.kafka.clients.producer.KafkaProducer.new(props)
    rescue => e
        logger.error("Unable to create Kafka producer from given configuration",
                     :kafka_error_message => e,
                     :cause => e.respond_to?(:getCause) ? e.getCause() : nil)
        raise e
    end
end
```

### Run producer from the command line

Run the following command to start the Logstash pipeline to produce events from stdin to your OSS topic:

```bash
logstash -f logstashProducer.conf
```

The Logstash producer will now begin reading events from stdin and sending events to the Kafka-enabled OSS at topic `mytopic`. If you would like to change the topic, change the `topic_id` value in `logstashProducer.config`.

## Logstash Consumer

Using the provided Logstash consumer example, receive messages from the Kafka-enabled OSS.

#### logstashConsumer.config

Update the `bootstrap_servers` value in `logstashConsumer.config` to direct the consumer to the OSS endpoint.

```
kafka {
    codec => json
    topics => ["mytopic"]
    bootstrap_servers => "streaming.{region}.oci.oraclecloud.com:9092"
    security_protocol => "SASL_SSL"
    sasl_mechanism => "PLAIN"
    auto_offset_reset => "earliest"
    jaas_path => "<path_to_jaas_file>"
}
```

Update the `password` value in `jaas.conf` to use the correct authentication.

```
KafkaClient {
    org.apache.kafka.common.security.plain.PlainLoginModule required
	username="{tenancyName}/{username}/{compartmentId}" 
	password="{authToken}";
};
```

#### Configuration issues

You may not be able to configure the jaas field due to this [Kafka input plugin issue](https://github.com/logstash-plugins/logstash-input-kafka/issues/311). In this case, one possible quick fix is to edit the `kafka.rb` file in `logstash-input-kafka` plugin folder, e.g. `logstash-6.6.1/vendor/bundle/jruby/2.3.0/gems/logstash-input-kafka-8.3.1/lib/logstash/inputs/kafka.rb`. You may directly set the `sasl.jaas.config` in the `create_consumer` function and comment out `set_trustore_keystore_config(props)` if you set `security_protocol` as `SASL_SSL`.

```
def create_consumer(client_id)
    begin
        props = java.util.Properties.new
        kafka = org.apache.kafka.clients.consumer.ConsumerConfig

        props.put(kafka::AUTO_COMMIT_INTERVAL_MS_CONFIG, auto_commit_interval_ms)
        props.put(kafka::AUTO_OFFSET_RESET_CONFIG, auto_offset_reset) unless auto_offset_reset.nil?
        props.put(kafka::BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers)
        props.put(kafka::CHECK_CRCS_CONFIG, check_crcs) unless check_crcs.nil?
        props.put(kafka::CLIENT_ID_CONFIG, client_id)
        props.put(kafka::CONNECTIONS_MAX_IDLE_MS_CONFIG, connections_max_idle_ms) unless connections_max_idle_ms.nil?
        props.put(kafka::ENABLE_AUTO_COMMIT_CONFIG, enable_auto_commit)
        props.put(kafka::EXCLUDE_INTERNAL_TOPICS_CONFIG, exclude_internal_topics) unless exclude_internal_topics.nil?
        props.put(kafka::FETCH_MAX_BYTES_CONFIG, fetch_max_bytes) unless fetch_max_bytes.nil?
        props.put(kafka::FETCH_MAX_WAIT_MS_CONFIG, fetch_max_wait_ms) unless fetch_max_wait_ms.nil?
        props.put(kafka::FETCH_MIN_BYTES_CONFIG, fetch_min_bytes) unless fetch_min_bytes.nil?
        props.put(kafka::GROUP_ID_CONFIG, group_id)
        props.put(kafka::HEARTBEAT_INTERVAL_MS_CONFIG, heartbeat_interval_ms) unless heartbeat_interval_ms.nil?
        props.put(kafka::KEY_DESERIALIZER_CLASS_CONFIG, key_deserializer_class)
        props.put(kafka::MAX_PARTITION_FETCH_BYTES_CONFIG, max_partition_fetch_bytes) unless max_partition_fetch_bytes.nil?
        props.put(kafka::MAX_POLL_RECORDS_CONFIG, max_poll_records) unless max_poll_records.nil?
        props.put(kafka::MAX_POLL_INTERVAL_MS_CONFIG, max_poll_interval_ms) unless max_poll_interval_ms.nil?
        props.put(kafka::METADATA_MAX_AGE_CONFIG, metadata_max_age_ms) unless metadata_max_age_ms.nil?
        props.put(kafka::PARTITION_ASSIGNMENT_STRATEGY_CONFIG, partition_assignment_strategy) unless partition_assignment_strategy.nil?
        props.put(kafka::RECEIVE_BUFFER_CONFIG, receive_buffer_bytes) unless receive_buffer_bytes.nil?
        props.put(kafka::RECONNECT_BACKOFF_MS_CONFIG, reconnect_backoff_ms) unless reconnect_backoff_ms.nil?
        props.put(kafka::REQUEST_TIMEOUT_MS_CONFIG, request_timeout_ms) unless request_timeout_ms.nil?
        props.put(kafka::RETRY_BACKOFF_MS_CONFIG, retry_backoff_ms) unless retry_backoff_ms.nil?
        props.put(kafka::SEND_BUFFER_CONFIG, send_buffer_bytes) unless send_buffer_bytes.nil?
        props.put(kafka::SESSION_TIMEOUT_MS_CONFIG, session_timeout_ms) unless session_timeout_ms.nil?
        props.put(kafka::VALUE_DESERIALIZER_CLASS_CONFIG, value_deserializer_class)

        props.put("security.protocol", security_protocol) unless security_protocol.nil?
        props.put("sasl.jaas.config", 'org.apache.kafka.common.security.plain.PlainLoginModule required username="{tenancyName}/{username}/{compartmentId}" password="{authToken}";')

        if security_protocol == "SSL"
            set_trustore_keystore_config(props)
        elsif security_protocol == "SASL_PLAINTEXT"
            set_sasl_config(props)
        elsif security_protocol == "SASL_SSL"
            #set_trustore_keystore_config(props)
            set_sasl_config(props)
        end

        org.apache.kafka.clients.consumer.KafkaConsumer.new(props)
    rescue => e
        logger.error("Unable to create Kafka consumer from given configuration",
                     :kafka_error_message => e,
                     :cause => e.respond_to?(:getCause) ? e.getCause() : nil)
        raise e
    end
end
```

### Run consumer from the command line

Run the following command to start the Logstash pipeline to consume events from your OSS topic:

```bash
logstash -f logstashConsumer.conf
```

The Logstash producer will now begin reading events from the Kafka-enabled OSS at topic `mytopic` and printing the events to stdout. If you would like to change the topic, change the `topics` value in `logstashConsumer.config`.

Check out [Kafka input plugin](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-kafka.html) and [Kafka output plugin](https://www.elastic.co/guide/en/logstash/current/plugins-outputs-kafka.html) for more detailed information on connecting Logstash to Kafka.
