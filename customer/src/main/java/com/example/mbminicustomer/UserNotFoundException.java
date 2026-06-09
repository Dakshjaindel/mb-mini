package com.example.mbminicustomer;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long phoneNo) {
        super("User not found with phone: " + phoneNo);
    }
}
