package com.example.mbminicart.Entities;


import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "logs")
public class Log extends Auditable {

    @Id
    private String id;

    private String message;

    public Log(String message){
        this.message=message;
    }

    // getters, setters, constructors
}
