package com.example.demo.exceptions;

public class ErrorException extends RuntimeException {
    public ErrorException(String message) {
        super(message);
    }
}