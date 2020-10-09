from kafka import KafkaConsumer


consumer = KafkaConsumer('igorTest', bootstrap_servers = 'streaming.uk-london-1.oci.oraclecloud.com:9092', 
                         security_protocol = 'SASL_SSL', sasl_mechanism = 'PLAIN',
                         consumer_timeout_ms = 10000, auto_offset_reset = 'earliest',
                         group_id='group-0',
                         sasl_plain_username = '{tenancyName}/{username}/{stream_pool_OCID}', 
                         sasl_plain_password = '{authToken}')

for message in consumer:
    print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition, message.offset, message.key, message.value)) 