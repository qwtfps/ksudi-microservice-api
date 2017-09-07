package com.ksudi.microservice.feign.support.Exception;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CallExceptionDecoder implements ErrorDecoder {

    public static CallExceptionDecoder instance = new CallExceptionDecoder();

    public static CallExceptionDecoder getInstance() {
        return instance;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = FeignException.errorStatus(methodKey, response);
        return new RemoteCallException(exception.getMessage(), exception);
    }
}
