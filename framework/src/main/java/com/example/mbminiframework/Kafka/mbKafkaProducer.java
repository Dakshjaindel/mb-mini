package com.example.mbminiframework.Kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class mbKafkaProducer {



    private final KafkaTemplate<String,Object> kafkaTemplate;


    @Autowired
    public mbKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void publish(String topic, String key, Object payload){
        log.info("Publishing to topic '{}' with key '{}': {}", topic, key, payload);
        kafkaTemplate.send(topic,key,payload);

    }


}
