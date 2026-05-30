package com.example.entryPool.exception;

public class LimitExceededException extends RuntimeException{
    public LimitExceededException(String message){
        super(message);
    }
}
