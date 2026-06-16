package com.example.mbminiframework.Configs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.mongodb.url}")
    private String MongoUrl;


    @Override
    public String getDatabaseName(){
        return "mb_mini_cart";
    }

    @Override
    @Bean
    public MongoClient mongoClient(){
        return MongoClients.create(MongoUrl);
    }
}
