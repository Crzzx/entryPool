package com.example.entryPool.exception;

public class NoAvailableSlotsException extends RuntimeException{
    public NoAvailableSlotsException(String message){
        super(message);
    }
}
