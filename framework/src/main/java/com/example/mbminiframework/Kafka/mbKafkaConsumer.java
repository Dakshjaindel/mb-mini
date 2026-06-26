package com.example.mbminiframework.Kafka;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;




@Service
@Slf4j
@RequiredArgsConstructor
public class mbKafkaConsumer {

    @Value("${spring.kafka.topic.name")
    private String topicName;

    @KafkaListener(topics = "${spring.kafka.topic.name}")
    public Object consume(Object message){
        return message;
    }


}
