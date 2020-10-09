from confluent_kafka import Consumer


conf = {
                'bootstrap.servers': 'streaming.eu-frankfurt-1.oci.oraclecloud.com:9092',
                'group.id': 'test1',
                'auto.offset.reset': 'earliest',
                'enable.auto.commit': 'True',
                'security.protocol': 'SASL_SSL',
                'sasl.mechanism': 'PLAIN',
                'sasl.username': '{tenancyName}/{username}/{stream_pool_OCID}',
                'sasl.password': '{authToken}'

            }

consumer = Consumer(conf)
consumer.subscribe(['igorTest'])

While True:
    message = consumer.consume(num_messages=600, timeout=30)
    for msg in message:
        print('Received message : {}{}'.format(msg.value().decode('utf-8'),msg.offset()))





