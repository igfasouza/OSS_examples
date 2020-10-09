from kafka import KafkaProducer


producer = KafkaProducer(bootstrap_servers = 'streaming.{region}.oci.oraclecloud.com:9092', 
                         security_protocol = 'SASL_SSL', sasl_mechanism = 'PLAIN', 
                         sasl_plain_username = '{tenancyName}/{username}/{stream_pool_OCID}', 
                         sasl_plain_password = '{authToken}')
key = 'Hello'.encode('utf-8')
data = 'Hello5'.encode('utf-8')

producer.send('igorTest', key=key, value=data)