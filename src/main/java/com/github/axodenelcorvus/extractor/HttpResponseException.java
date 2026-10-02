package com.github.axodenelcorvus.extractor;

public class HttpResponseException extends RuntimeException {
    public HttpResponseException(String message, int code) {
        super(code + " | " + message);
    }
}
