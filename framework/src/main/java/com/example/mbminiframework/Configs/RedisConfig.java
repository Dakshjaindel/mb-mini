package com.example.mbminiframework.Configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.database}") int database
    ){
        JedisPoolConfig poolConfig= new JedisPoolConfig();
        poolConfig.setJmxEnabled(false);
        return new JedisPool(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT,null,database);


    }
}
