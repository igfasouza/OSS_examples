from confluent_kafka import Producer
import socket


conf = {
                'bootstrap.servers': 'streaming.eu-frankfurt-1.oci.oraclecloud.com:9092',
                'group.id': 'test1',
                'security.protocol': 'SASL_SSL',
                'sasl.mechanism': 'PLAIN',
                'sasl.username': '{tenancyName}/{username}/{stream_pool_OCID}',
                'sasl.password': '{authToken}'

            }

producer = Producer(conf)

topic = 'igorTest'
Key = 'hello'
Value = 'hello'
producer.produce(topic, key="key", value="value")
