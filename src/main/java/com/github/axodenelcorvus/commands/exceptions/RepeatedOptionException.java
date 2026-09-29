package com.github.axodenelcorvus.commands.exceptions;

public class RepeatedOptionException extends IllegalArgumentException {
    public RepeatedOptionException(String message) {
        super(message);
    }
}
