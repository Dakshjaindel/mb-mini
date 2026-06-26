package com.example.mbmini;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class CatalogKafkaConfig {

    public static final String updateTopicName="basketDeplete";

    @Bean
    public NewTopic topicBuild(){
        return new NewTopic(updateTopicName,1,(short) 1);
    }
}
