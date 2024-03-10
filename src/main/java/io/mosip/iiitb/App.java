package io.mosip.iiitb;

import java.util.Base64;
import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.lib.MessageBrokerWrapper;
import io.mosip.iiitb.utils.RSACryptoTool;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class App {
    public static void main(String[] args) throws Exception {
//        createKeys();
//        System.exit(0);
        Injector injector = Guice.createInjector(new AppModule());
        OnDemandAppConfig config = injector.getInstance(OnDemandAppConfig.class);
        String privateKeyPath = config.privateKeyFileLocation();

        produceMessage(config);

        MessageBrokerWrapper broker = injector.getInstance(MessageBrokerWrapper.class);
        broker.start();

//        RSACryptoTool cryptoTool =  new RSACryptoTool(privateKeyPath);
//        //String encrypted_message_base64 = "SMM34PW1uPMhwoDby1D0BnB7NimvCDe9lGzBdCMTMpVpjfecNQSD04bRWn9m8ebz9FQuWOEzPjrUb7P4dy8RKPFwLvnDDBhr5Xu5D6yq9I1J5eZlujoQSte2lB7y/Bw26Mf7aelTjSxDc4yHIt2KPv9+EHmlh2XuC6lEZ1z3Ug+6rMH4LbxC5TutL+IciGyi8vu+NCiV76W0ZLpcyAbSwqgnmVfTY0yLhJOvCgpOsnFTRb8le4UX2dDNPPCjenIaAFQpkJpoZoma/CsiieE2bzXhz/3J3xl43DFpAIMJXrfZF9bky6Tp71ELXIT9y//j3WoJcsQgtWszDHkwY8GIKIDRDl6SauH4n8fUZaWjTZRcSguRGRgX7k9EQy4J9kY+YW79C2EFQh6uGtK3dI5xrl0FFCn5ro6QKPRpx6BlYHULsIEaKl/+B+Kv2XAwKFkiFx+/DSkMag1U54sar7e0fC4ThAgUdRBEv7Nhh8HmstX3Z10uFMfN0tU9WQrEiOsCkw0OTqX9KpqFDGrFfNFClz0N0QKrMcVzoLlFqlAxPuCCunVZAzOJIWBfixaq8yiv/bwUe6F9CIwmCPr1ePZHCVVfuGzgfKuwx9iJcynHO/+BP5hpkH/0hw2mDdZUVH8wdz9YQ3qlnXtTNPERl69+bQ6PUV87pey0DE2AAwfJdmY=";
//
//        byte[] message = Base64.getDecoder().decode(encrypted_message_base64);
//        byte[] decryptedData = cryptoTool.decryptData(message);
//        System.out.println(" ==> " + new String(decryptedData));
    }

    private static void produceMessage(OnDemandAppConfig config) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        String topic = config.kafkaTopic();
        String multilineMessage = "        {\n" +
                                  "        	\"publisher\": \"IDA\",\n" +
                                  "        	\"topic\": \"AUTHENTICATION_ERRORS\",\n" +
                                  "        	\"publishedOn\": \"2024-03-03T22:50:50.116Z\",\n" +
                                  "        	\"event\": {\n" +
                                  "        		\"id\": \"69833e71-394b-4cb4-9e96-a2138594be5e\",\n" +
                                  "        		\"transactionId\": null,\n" +
                                  "        		\"type\": null,\n" +
                                  "        		\"timestamp\": \"2024-03-03T22:50:50.116Z\",\n" +
                                  "        		\"dataShareUri\": null,\n" +
                                  "        		\"data\": {\n" +
                                  "        			\"error_message\": \"VID not available in database\",\n" +
                                  "        			\"error_Code\": \"IDA-MLC-018\",\n" +
                                  "        			\"entityName\": \"mosip_partnerorg1709505095935\",\n" +
                                  "        			\"requestdatetime\": \"2024-03-03T22:50:50.115Z\",\n" +
                                  "        			\"individualId\": \"SMM34PW1uPMhwoDby1D0BnB7NimvCDe9lGzBdCMTMpVpjfecNQSD04bRWn9m8ebz9FQuWOEzPjrUb7P4dy8RKPFwLvnDDBhr5Xu5D6yq9I1J5eZlujoQSte2lB7y/Bw26Mf7aelTjSxDc4yHIt2KPv9+EHmlh2XuC6lEZ1z3Ug+6rMH4LbxC5TutL+IciGyi8vu+NCiV76W0ZLpcyAbSwqgnmVfTY0yLhJOvCgpOsnFTRb8le4UX2dDNPPCjenIaAFQpkJpoZoma/CsiieE2bzXhz/3J3xl43DFpAIMJXrfZF9bky6Tp71ELXIT9y//j3WoJcsQgtWszDHkwY8GIKIDRDl6SauH4n8fUZaWjTZRcSguRGRgX7k9EQy4J9kY+YW79C2EFQh6uGtK3dI5xrl0FFCn5ro6QKPRpx6BlYHULsIEaKl/+B+Kv2XAwKFkiFx+/DSkMag1U54sar7e0fC4ThAgUdRBEv7Nhh8HmstX3Z10uFMfN0tU9WQrEiOsCkw0OTqX9KpqFDGrFfNFClz0N0QKrMcVzoLlFqlAxPuCCunVZAzOJIWBfixaq8yiv/bwUe6F9CIwmCPr1ePZHCVVfuGzgfKuwx9iJcynHO/+BP5hpkH/0hw2mDdZUVH8wdz9YQ3qlnXtTNPERl69+bQ6PUV87pey0DE2AAwfJdmY=\",\n" +
                                  "        			\"authPartnerId\": \"mosip_partnerorg1709505095935\",\n" +
                                  "        			\"individualIdType\": \"VID\",\n" +
                                  "        			\"requestSignature\": \"eyJ4NWMiOlsiTUlJRHpEQ0NBclNnQXdJQkFnSUlzTDBkbzNYak0vZ3dEUVlKS29aSWh2Y05BUUVMQlFBd2RqRUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeElEQWVCZ05WQkFzTUYwMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU0lDaFFUVk1wTVJVd0V3WURWUVFEREF4M2QzY3ViVzl6YVhBdWFXOHdIaGNOTWpRd016QXpNakl6TVRVeVdoY05NalV3TXpBek1qSXpNVFV5V2pDQmtURUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1TWXdKQVlEVlFRS0RCMXRiM05wY0Y5d1lYSjBibVZ5YjNKbk1UY3dPVFV3TlRBNU5Ua3pOVEVhTUJnR0ExVUVDd3dSU1VSQkxWUkZVMVF0VDFKSExWVk9TVlF4TVRBdkJnTlZCQU1NS0ZCQlVsUk9SVkl0Y25BdGJXOXphWEJmY0dGeWRHNWxjbTl5WnpFM01EazFNRFV3T1RVNU16VXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFDWXA5UFZlUWVIc0pJMDZLV1dOUzdwRDFnVWdvclNoL2txS1dLYlJ1NVpmOUhhVVZUdVExWXh6UFZDKytZRkdwajFKejNCRS9vcStlRWptWWJMakgwaTl6TTk5RWZvWGlLdmE1ZitsVFFzaGF5RXBoOHA3TlI3azJ4YUV6eVB1YlUyQU5UNnZSeUlNVVRtWUxNKzBIbW83Tzl4VUllRlZvYkNOcnBYTzFVYVloVzRNMGFoTkZCSmtoYzVoemFqQkVoY0gzS0N3TUVTcDJnZk9XdndXOU9nSEVBWlBGSHFkemlCK3BmcWhnaWQ4MFNWSG9ZK25OMnc1dVVHTDk2UEkrWnRrV2NBaU1xakpYQkd2eWxqaVF3ZUx4dkRFcU1wbTR2VXNDSGNVNGpyWWEwM0JCS0VLVWZZb3BXdy9iMjhoZFE2KzJmMFd5OU16TEUwcDFTMmF0MmJBZ01CQUFHalFqQkFNQThHQTFVZEV3RUIvd1FGTUFNQkFmOHdIUVlEVlIwT0JCWUVGSXY2RzRjVTQ1QkhwOEx2dUQ1dlIxY1cxRnB6TUE0R0ExVWREd0VCL3dRRUF3SUNwREFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBS1cya1lmU0JmK3JYRjNBcHBOcU5WZ0RIdjVHTlI1QldtZnl0ZkJqVHhKeDBRSDd0Q1l5eGI1dER4K25VcVl5RjFyNklBbktyOFFwZ3BUV1NLMEwzMGRKbVJKYUFWMWxpckgyQUtPcm9kWDhHelNiMHVOV1V6ZTQxN2htUkNKQ0g1VlVJaWI0ZnR6d2hWcmdmVWV4Q2dObzB3bG5TZGdZQWZvYTF6OEMwWm5DTW9SeG9KeXhweFFCUjZSMHkrMjdWMWNTNUgzcXhSTmRudDBhMW5kRXBIMnJKS1FPSUFxbHF3dzRJREVBNURGeEo0UmJFbWp5UzZORDdkaGM0akJKRGE2YW5uN3FRZjhSdkhQdEZ6RUowZUNTM1NCbFR5K055eFo1ajRyRG16V2c3ODFadFZ3T01XT1dxL0RvNngxdFlHZzJ6L1IvUy9pcmVFR0dZT29saEFnPT0iXSwiYWxnIjoiUlMyNTYifQ..DkDDmwENduhe_RY0ZMEYMOEk_aP8dANf8RpGjJS2RQbpr7XOYL76EzM1_HUD92lbVa6FXSRL4XGQwia6C3WR9HdoU-2Ekad1urgqaXja8ZIEnUVOmk21Vg2h5PQoz4M5lRgp55wDZ1GkgQITBZLBWdGGOYfUk9QJYY1dAxFZ7qpNB7aar82oaNv_b62xSfIkdqT5cOMG8wPcCxZz_p2SRMJfpDg8zSEE80dMxlKhCbfp6y7k912DtcslZhRuRof0VO7xwrh_Y33n_Wmf_NGHMVJFauyGhm8MbAijSnVr18LoWBrEo_1DOoQuHOFtJ3oyp-CazxRqC0bPeqpd2GKFdg\"\n" +
                                  "        		}\n" +
                                  "        	}\n" +
                                  "        }\n";

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, multilineMessage);
        producer.send(record);
        producer.close();
    }
}