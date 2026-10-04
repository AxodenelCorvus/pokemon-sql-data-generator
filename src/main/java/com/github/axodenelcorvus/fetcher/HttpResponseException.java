package com.github.axodenelcorvus.fetcher;

public class HttpResponseException extends RuntimeException {
    public HttpResponseException(String message, int code) {
        super(code + " | " + message);
    }
}
