package com.example.demo.exceptions;

public class RunNotFoundException extends RuntimeException{
    public RunNotFoundException(){
        super("Run Not Found");
    }
}
