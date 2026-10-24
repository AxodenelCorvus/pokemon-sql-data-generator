package com.github.axodenelcorvus.fetcher;

public class BadHttpResponseException extends RuntimeException {
    public BadHttpResponseException(String message, int code) {
        super(code + " | " + message);
    }
}
