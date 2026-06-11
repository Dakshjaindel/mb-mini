package com.example.mbminicustomer.Exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long phoneNo) {
        super("User not found with phone: " + phoneNo);
    }
}
