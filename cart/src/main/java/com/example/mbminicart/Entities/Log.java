package com.example.mbminicart.Entities;


import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Data
@Document(collection = "logs")
public class Log {

    @Id
    private String id;

    private String message;

    public Log(String message){
        this.message=message;
    }

    // getters, setters, constructors
}
