package com.ksudi.microservice.feign.support.Exception;

public class RemoteCallException extends RuntimeException {


    public RemoteCallException(String message, Throwable cause) {
        super(message, cause);
    }


}
